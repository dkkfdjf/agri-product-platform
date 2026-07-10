package com.agri.platform.service.impl;

import com.agri.platform.dto.AddressRequest;
import com.agri.platform.entity.Address;
import com.agri.platform.mapper.AddressMapper;
import com.agri.platform.service.AddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void addAddress(String userName, AddressRequest request) {
        Address address = new Address();
        address.setOwnName(userName);
        address.setConsignee(request.getConsignee());
        address.setPhone(request.getPhone());
        address.setAddressDetail(request.getAddressDetail());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        addressMapper.insert(address);
    }

    @Override
    public List<Address> getAddressList(String userName) {
        return addressMapper.selectList(
            new LambdaQueryWrapper<Address>()
                .eq(Address::getOwnName, userName)
                .orderByDesc(Address::getIsDefault)
        );
    }

    @Override
    public void updateAddress(Integer id, String userName, AddressRequest request) {
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getOwnName().equals(userName)) {
            throw new RuntimeException("地址不存在或无权限");
        }
        if (request.getConsignee() != null) address.setConsignee(request.getConsignee());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getAddressDetail() != null) address.setAddressDetail(request.getAddressDetail());
        if (request.getIsDefault() != null) address.setIsDefault(request.getIsDefault());
        addressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Integer id, String userName) {
        Address address = addressMapper.selectById(id);
        if (address == null || !address.getOwnName().equals(userName)) {
            throw new RuntimeException("地址不存在或无权限");
        }
        addressMapper.deleteById(id);
    }

    @Override
    public void setDefaultAddress(Integer id, String userName) {
        // 取消其他默认地址
        addressMapper.update(null,
            new LambdaUpdateWrapper<Address>()
                .eq(Address::getOwnName, userName)
                .set(Address::getIsDefault, 0));
        // 设置新默认地址
        Address address = addressMapper.selectById(id);
        if (address != null && address.getOwnName().equals(userName)) {
            address.setIsDefault(1);
            addressMapper.updateById(address);
        }
    }
}