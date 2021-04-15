package hk.onedegree.web.springboot.controller;

import hk.onedegree.web.springboot.dev.User;
import hk.onedegree.web.springboot.dev.UserRepository;
import hk.onedegree.web.springboot.requestbody.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.UUID;

@RestController // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after
public class DevController {
    @Inject // This means to get the bean called userRepository
    private UserRepository userRepository;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewUser (@RequestBody RegisterRequest registerRequest) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        User n = new User();
        n.setName(UUID.randomUUID().toString());
        n.setEmail(registerRequest.getEmail());
        userRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}
