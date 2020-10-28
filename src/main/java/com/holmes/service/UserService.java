package com.holmes.service;

public interface UserService {

    /**
     * @description: 保存用户访问次数
     * @param userId 用户id
     * @return {@link int}
     * @author: holmes
     * @date: 2020/10/28 11:29 上午
     */
    int saveUserCount(Integer userId);

    /**
     * @description: 判断调用次数是否超时
     * @param userId 用户id
     * @return {@link boolean} true 超时
     * @author: holmes
     * @date: 2020/10/28 11:29 上午
     */
    boolean getUserCount(Integer userId);
}
