package com.kob.backend.controller.pk.user;

import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/all")
    public List<User> getAll() {
        return userMapper.selectList(null);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userMapper.selectById(id);
    }

    @GetMapping("/add/{id}/{name}/{password}")
    public String addUser(@PathVariable Integer id, @PathVariable String name, @PathVariable String password) {
        // 密码加密
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(id, name, encodedPassword);
        userMapper.insert(user);

        return "Success!";
    }

    @GetMapping("/delete/{id}")
    public String deleteUserById(@PathVariable Integer id) {
        userMapper.deleteById(id);
        return "Success!";
    }

}
