package cn.tedu.store.controller;

import cn.tedu.store.controller.ex.InvalidParameterException;
import cn.tedu.store.entity.User;
import cn.tedu.store.service.IUserService;
import cn.tedu.store.service.ex.*;
import cn.tedu.store.service.impl.UserServiceImpl;
import cn.tedu.store.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 处理用户模块的控制器类
 */
//@Controller注解，是Spring框架提供的注解，该注解书写在类上，其作用是
//自动创建一个该类的对象，放到框架的容器中。如果没有此注解，则下面的
//@Autowire注解无效。
//@Controller
@RestController //等价于@Controller + @ReponseBody
//@RequestMapping注解，写在控制器类上，表示该类处理以指定url开头的所有请求
@RequestMapping("/user")
@Validated
public class UserController extends BaseController{ //ProductController,CartController

	@Autowired
	private IUserService userService;

	//@RequestMapping注解，写在方法上，表示方法将处理的具体url请求
	//若所在类上同时也有该注解，则具体的url前包含类上面该注解中指定的url部分
	//@RequestMapping注解，写在方法上时，在调用方法执行时，会自动提供满足方法要求的实参值
	//@PostMapping注解与@RequestMapping作用相同，但只接收post类型的请求
	@PostMapping("/register")
	//@ResponseBody注解，写在方法上，表示将方法的返回值作为响应体内容（发送给浏览器）
	//@ResponseBody
	public R<Void> register(@Valid User user, BindingResult result){
        /*
        if (result.hasErrors()){ //如果参数验证存在错误
            String message = result.getFieldError().getDefaultMessage();
            throw new InvalidParameterException(message);
        }*/
		userService.register(user);
		return new R<>(OK) ;
	}

	/**
	 * 处理登录请求
	 * @param username 用户名
	 * @param password 密码
	 * @return 登录用户数据
	 */
	@PostMapping("/login")
	//@ResponseBody
	public R<User> login(@NotBlank(message = "用户名不能为空!") String username,
						 @Size(min = 6, max = 15 , message = "密码必须在6到15个字符之间!" ) String password
			, HttpSession session){
		User data = userService.login(username, password);
		//登录成功，保存用户的登录信息
		session.setAttribute("uid",data.getId());
		return new R<>(OK,data);
	}

	@PostMapping("/change_password")
	public R<Void>changePassword(String oldPassword, String newPassword, HttpSession session){
		Integer id = getUidFromSession(session);
		//调用业务层功能修改密码
		userService.changePassword(id, oldPassword, newPassword);
		//响应结果给浏览器
		return new R<>(OK);
	}

	@GetMapping("/get_by_id")
	public R<User> getById(HttpSession session){
		Integer id = getUidFromSession(session);
		User data = userService.getById(id);
		return new R<>(OK,data);
	}

	@PostMapping("/changeInfo")
	public R<Void> changInfoById(User user, HttpSession session){
		Integer id = getUidFromSession(session);
		userService.changeInfoById(id, user);
		return new R<>(OK);
	}
}
