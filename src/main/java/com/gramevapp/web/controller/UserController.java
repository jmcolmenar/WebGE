package com.gramevapp.web.controller;

import com.gramevapp.web.model.*;
import com.gramevapp.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/user/profile")
    public String userProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {    // User not authenticated
            System.out.println("User not authenticated");
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName());
        //UserDetails user = myUserDetailsService.loadUserByUsername(authentication.getName());

        model.addAttribute("userLogged", user);
        //userService.loadUserByUsername(authentication.getName());
        return "user/profile";
    }

    @RequestMapping(value="/user/updateUserPassword", method=RequestMethod.POST)
    public String updateUserPassword(@ModelAttribute("userPassword") @Valid UserUpdatePasswordDto userUpDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {    // User not authenticated
            System.out.println("User not authenticated");
            return "redirect:/login";
        }
        User user = userService.findByEmail(authentication.getName());
        if(userUpDto.getPassword().equals(userUpDto.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(userUpDto.getPassword()));
            userService.save(user);
        }

        return "redirect:/user/profile";
    }

    @RequestMapping(value="/user/updateStudy", method=RequestMethod.POST)
    public String updateUserStudy(Model model,
                                  @ModelAttribute("userStudy") @Valid UserUpdateStudyDto userUpDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {    // User not authenticated
            System.out.println("User not authenticated");
            return "redirect:/login";
        }
        User user = userService.findByEmail(authentication.getName());
        user.setStudyInformation(userUpDto.getStudyInformation());
        user.setWorkInformation(userUpDto.getWorkInformation());

        userService.save(user);

        model.addAttribute("userLogged", user);     // If we don't set the model. In ${userLogged.getUsername()}" we will have fail
        model.addAttribute("message", "Study/Work user information updated");

        return "redirect:/user/profile";
    }

    @RequestMapping(value="/user/updateUserBasicInfo",method=RequestMethod.POST)
    public String updateUserInformation(Model model,
                                        @ModelAttribute("userBasicInfo") @Valid UserUpdateBasicInfoDto userUpDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {    // User not authenticated
            System.out.println("User not authenticated");
            return "redirect:/login";
        }
        User user = userService.findByEmail(authentication.getName());
        user.setFirstName(userUpDto.getFirstName());
        user.setLastName(userUpDto.getLastName());
        user.setPhone(userUpDto.getPhone());
        user.setAddressDirection(userUpDto.getAddressDirection());
        user.setState(userUpDto.getState());
        user.setCity(userUpDto.getCity());
        user.setZipcode(userUpDto.getZipcode());

        if(userService.findByEmail(userUpDto.getEmail())==null)
            user.setEmail(userUpDto.getEmail());

        userService.save(user);

        model.addAttribute("userLogged", user);     // If we don't set the model. In ${userLogged.getUsername()}" we will have fail
        // ESTARÍA BIEN PONER UN MENSAJE DE QUE SE HA ACTUALIZADO LA INFORMACIÓN
        model.addAttribute("message", "Basic user information updated");
        return "/user/profile";
    }

    @RequestMapping(value="/user/updateAboutMe", method= RequestMethod.POST)
    public String updateAboutMe(Model model,
                                @ModelAttribute("userAboutMe") @Valid UserUpdateAboutDto userUpDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findByEmail(authentication.getName());
        user.setAboutMe(userUpDto.getAboutMe());

        userService.save(user);

        model.addAttribute("userLogged", user);
        model.addAttribute("message", "About me user information updated");
        return "/user/profile";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // https://stackoverflow.com/questions/26101738/why-is-the-anonymoususer-authenticated-in-spring-security
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/user";
        }

        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }

    @ModelAttribute("user") // Without this. The registration won't work
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping("/registration")
    public String showRegistrationForm() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // https://stackoverflow.com/questions/26101738/why-is-the-anonymoususer-authenticated-in-spring-security
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/user";
        }

        return "userRegistration";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
                                      BindingResult result){

        User existingEmail = userService.findByEmail(userDto.getEmail());
        User existingUsername = userService.findByUsername(userDto.getUsername());

        if(existingEmail != null){
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if(existingUsername != null){
            result.rejectValue("username", null, "There is already an account registered with that username");
        }

        if (result.hasErrors()){
            return "userRegistration";
        }

        userService.save(userDto);
        return "login";
        //return "redirect:/userRegistration?success";
    }

    /**
     *
     return "redirect:/books";

     It returns to the client (browser) which interprets the http response and automatically calls the redirect URL

     return "jsp/books/booksList";

     It process the JSP and send the HTML to the client

     return "forward:/books";

     It transfer the request and calls the URL direct in the server side.
     *
     */
}