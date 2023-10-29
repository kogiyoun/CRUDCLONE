package miniproject.demo.controller;

import lombok.RequiredArgsConstructor;
import miniproject.demo.dto.LoginInfo;
import miniproject.demo.dto.User;
import miniproject.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    //http://localhost:7793/userRegForm
    //classpath:/templates/userRegForm.html
    @GetMapping("/userRegForm")
    public String userRegForm(){
        return "userRegForm";

    }

    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
            //어떤 기능이 필요한지 미리 알 수  있다. => 인터페이스를 만드는 이유
            //회원 정보 저장하기
    ){
        System.out.println("name = " + name + ", email = " + email + ", password = " + password);

        userService.addUser(name, email, password);
        return "redirect:/welcome";// 브라우저에게 자동으로 http://localhost:7793/welcome으로 이동해라
    }

   //http://localhost:7793/welcome
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession //스프링이 자동으로 세션을 처리하는 httpsession 객체를 넣어줌
    ){
        //email에 해당하는 회원 정보를 읽어온 후
        //아이디 암호가 맞다면 세션에 회원정보를 저장한다.
        System.out.println("email = " + email + ", password = " + password);

        try{
            User user = userService.getUser(email);
            if(user.getPassword().equals(password)){
                System.out.println("암호일치");
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());
                //권한정보를 읽어와서 loginInfo에 추가하기
                List<String> roles = userService.getRoles(user.getUserId());


                httpSession.setAttribute("loginInfo", loginInfo);
                System.out.println("세션에 로그인 정보 저장 성공");
            }else{
                throw new RuntimeException("암호가 다릅니다.");
            }
        }catch (Exception ex){
            return "redirect:/loginForm?error=true";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession){
        //세션에서 회원정보를 삭제한다
        httpSession.removeAttribute("loginInfo");
        return "redirect:/";
    }
}
