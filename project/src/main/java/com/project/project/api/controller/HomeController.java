package com.project.project.api.controller;




import org.springframework.stereotype.Controller; // IMPORTANT: Use @Controller, not @RestController for serving HTML templates
import org.springframework.web.bind.annotation.GetMapping;

@Controller // This annotation tells Spring that this class will handle web requests and return view names (like HTML files)
public class HomeController {

    @GetMapping("/") // Maps the root URL (e.g., http://localhost:8080/)
    public String homePage() {
        return "x"; // Returns the template named "x.html"
    }

    @GetMapping("/x.html") // Maps /x.html directly
    public String xHtmlPage() {
        return "x";
    }

    @GetMapping("/res.html") // Maps /res.html
    public String resHtmlPage() {
        return "res";
    }

    @GetMapping("/addreview.html") // Maps /add_review.html
    public String addReviewHtmlPage() {
        return "addreview";
    }

    @GetMapping("/login.html") // Maps /login.html
    public String loginPage() {
        return "login";
    }

    @GetMapping("/sign-up.html") // Maps /signup.html
    public String signupPage() {
        return "sign-up";
    }

    @GetMapping("/create-post.html") // Maps /create-post.html
    public String createPostPage() {
        return "create-post";
    }

    @GetMapping("/Add place.html") // Maps /Add place.html (mind the space)
    public String addPlacePage() {
        return "Add place"; // Return the exact filename
    }

    @GetMapping("/resp.html") // Maps /resp.html
    public String respPage() {
        return "resp";
    }

    @GetMapping("/new.html") // Maps /new.html
    public String newPage() {
        return "new";
    }

    @GetMapping("/profile.html") // Maps /profile.html
    public String profilePage() {
        return "profile";
    }

    @GetMapping("/Aboutus.html") // Maps /Aboutus.html
    public String aboutUsPage() {
        return "Aboutus";
    }

    // NEW MAPPINGS FOR THE HTML FILES YOU JUST PROVIDED:
    @GetMapping("/user,p.html") // Maps /user.html
    public String userPage() {
        return "user,p";
    }

    @GetMapping("/p.html") // Maps /p.html
    public String pPage() {
        return "p";
    }

    @GetMapping("/op.html") // Maps /op.html
    public String opPage() {
        return "op";
    }
}