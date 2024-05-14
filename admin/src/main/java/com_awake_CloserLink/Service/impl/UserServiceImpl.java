package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Common.Convention.Exception.ClientException;
import com_awake_CloserLink.Dto.Request.UserRegister;
import com_awake_CloserLink.Dto.Respons.UserRespDTO;
import com_awake_CloserLink.Entitys.UserDO;
import com_awake_CloserLink.Mapper.UserMapper;
import com_awake_CloserLink.Service.UserService;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com_awake_CloserLink.Common.Enums.UserErrorCodeEnum.USER_NAME_EXIST_ERROR;

/**
 * @Author 清醒
 * @Date 2024/5/14 9:52
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    @Autowired
    private RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    public UserRespDTO getUsernameByUserName(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO != null) {
            UserRespDTO userRespDTO = new UserRespDTO();
            BeanUtils.copyProperties(userDO, userRespDTO);
            return userRespDTO;
        }
        return null;

    }

    /**
     * 判断用户名是否存在
     *
     * @param username
     * @return
     */
    @Override
    public Boolean isHasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }
    /**
     * 注册用户
     */
    @Override
    public void register(UserRegister userRegister) {
        int insert = baseMapper.insert(BeanUtil.toBean(userRegister,UserDO.class));
        //加入布隆过滤器
        if (insert <1) {
          throw  new ClientException(USER_NAME_EXIST_ERROR);
        }

    }


}