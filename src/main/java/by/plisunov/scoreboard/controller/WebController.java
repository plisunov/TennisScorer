package by.plisunov.scoreboard.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Basic spring MVC Controller
 *
 * @author Andrey
 */
@Controller
public class WebController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String start() {
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView goTomainPage() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //User user = userRepository.findUserByUsername(auth.getName());
        //modelAndView.addObject("userName", "Welcome " + user.getUsername() + ")");
        //modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping(value = "/football", method = RequestMethod.GET)
    public String footballInfoPage() {
        return "football";
    }

    @RequestMapping(value = "/testWS", method = RequestMethod.GET)
    public String goToTestPage() {
        return "testWS";
    }

    @GetMapping("/403")
    public String error403() {
        return "/error/403";
    }

    @GetMapping("/404")
    public String error404() {
        return "/error/404";
    }
}