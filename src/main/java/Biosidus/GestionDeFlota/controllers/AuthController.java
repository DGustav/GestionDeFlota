package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {
    @GetMapping("/")
    public String loginGet(Model model) {
        return "login";
    }

    @GetMapping("/tableroDeControl")
    public String tableroDeControl(Model model) {
        return "tableroDeControl";
    }

    @GetMapping("/alertas")
    public String alertas(Model model) {
        return "alertas";
    }

    @GetMapping("/forbidden")
    public String forbidden() {
        return "forbidden";
    }
}
