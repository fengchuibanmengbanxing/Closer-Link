package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Common.Convention.Exception.ClientException;
import com_awake_CloserLink.Dto.Request.UserLoginReqDTO;
import com_awake_CloserLink.Dto.Request.UserRegister;
import com_awake_CloserLink.Dto.Request.UserUpdateReqDTO;
import com_awake_CloserLink.Dto.Respons.UserLoginRespDTO;
import com_awake_CloserLink.Dto.Respons.UserRespDTO;
import com_awake_CloserLink.Entitys.UserDO;
import com_awake_CloserLink.Mapper.UserMapper;
import com_awake_CloserLink.Service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com_awake_CloserLink.Common.Constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com_awake_CloserLink.Common.Enums.UserErrorCodeEnum.USER_NAME_EXIST_ERROR;

/**
 * @Author 清醒
 * @Date 2024/5/14 9:52
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    @Autowired
    private RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
        //布隆过滤器是否包含用户名包含则取反注册失败
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }


    /**
     * 注册用户
     */
    @Override
    public void register(UserRegister userRegister) {
        //注册前查看用户名是否存在
        if (!isHasUsername(userRegister.getUsername())) {
            throw new ClientException(USER_NAME_EXIST_ERROR);
        }

        //获取互斥锁
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + userRegister.getUsername());

        try {
            if (lock.tryLock()) {
                //写入数据库
                int insert = baseMapper.insert(BeanUtil.toBean(userRegister, UserDO.class));
                if (insert < 1) {
                    throw new ClientException(USER_NAME_EXIST_ERROR);
                }
                //加入布隆过滤器
                userRegisterCachePenetrationBloomFilter.add(userRegister.getUsername());
            } else {
                throw new ClientException(USER_NAME_EXIST_ERROR);
            }
        } finally {
            lock.unlock();
        }

    }

    @Override
    public int update_user(UserUpdateReqDTO userUpdateReqDTO) {
        //TODO 判断修改用户的信息是否是当前用户的
        LambdaQueryWrapper<UserDO> updateWrapper
                = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, userUpdateReqDTO.getUsername());
        int update = baseMapper.update(BeanUtil.toBean(userUpdateReqDTO, UserDO.class), updateWrapper);

        return update;
    }
    //用户登录
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        //校验用户登录状态
        LambdaQueryWrapper<UserDO> loginWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, userLoginReqDTO.getUsername())
                .eq(UserDO::getPassword, userLoginReqDTO.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(loginWrapper);

        if (userDO == null) {
            throw new ClientException("用户不存在！");
        }
        //避免第三方多次登录使用hash结构
        //将登录用户存放入redis
        String uuid = UUID.randomUUID().toString().replace("-","");
        stringRedisTemplate.opsForHash().put("login:" + userLoginReqDTO.getUsername(), uuid,JSON.toJSONString(userDO));
        stringRedisTemplate.expire("login:" + userLoginReqDTO.getUsername(), 30, TimeUnit.MINUTES);
        return new UserLoginRespDTO(uuid);

    }
    //检查用户登录状态
    @Override
    public Boolean checkLogin(String token,String username) {
        Object remoteToken = stringRedisTemplate.opsForHash().get("login:" + username, token);
        if (remoteToken != null) {
            return true;
        }
        return false;
    }


    //用户退出
    @Override
    public void logout(String token, String username) {
        //检查用户登录状态
        if(checkLogin(token,username)){
          stringRedisTemplate.delete("login:" + username);
        }else {
            throw new ClientException("用户未登录");
        }

    }

}