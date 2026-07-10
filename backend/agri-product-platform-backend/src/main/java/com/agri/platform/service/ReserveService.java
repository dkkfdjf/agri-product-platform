package com.agri.platform.service;

import com.agri.platform.dto.ReserveRequest;
import com.agri.platform.entity.Reserve;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ReserveService {
    void makeReservation(String userName, ReserveRequest request);
    Page<Reserve> getReserveList(String userName, int page, int pageSize);
    Page<Reserve> getReserveListByExpert(String expertName, int page, int pageSize);
    void cancelReserve(Integer reserveId, String userName);
    void confirmReserve(Integer reserveId, String expertName, Integer status);
    void confirmReserve(Integer reserveId, String expertName, Integer status, String answer);
}
