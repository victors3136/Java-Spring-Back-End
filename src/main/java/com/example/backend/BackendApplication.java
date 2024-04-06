package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @GetMapping("/home")
    public String home() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>L04</title>
                    <style>
                        body {
                            background-color: black;
                            height: 100%;
                            width: 100%;
                        }

                        #square {
                            position: absolute;
                            background-color: red;
                            left: 13%;
                            top: 85%;
                            width: 90px;
                            height: 90px;
                        }
                    </style>
                </head>
                <body>
                <div id="square"></div>
                </body>
                <script>
                    const dragElement = (element) => {
                        let xInitial = 0, yInitial = 0, xNew = 0, yNew = 0;
                        const dragMouseDown = (event) => {
                            event.preventDefault();
                            xNew = event.clientX;
                            yNew = event.clientY;
                            document.onmouseup = closeDragElement;
                            document.onmousemove = elementDrag;
                        }
                        const elementDrag = (event) => {
                            event.preventDefault();
                            xInitial = xNew - event.clientX;
                            yInitial = yNew - event.clientY;
                            xNew = event.clientX;
                            yNew = event.clientY;
                            element.style.top = (element.offsetTop - yInitial) + "px";
                            element.style.left = (element.offsetLeft - xInitial) + "px";
                        }
                        const moveElementDownABit = (element) => {
                            if (element.offsetTop >= (window.innerHeight - element.clientHeight))
                                return;
                            element.style.top = `${element.offsetTop + 3}px`;
                            setTimeout(() => moveElementDownABit(element), 10);
                        }
                        const closeDragElement = () => {
                            document.onmouseup = undefined;
                            document.onmousemove = undefined;
                            moveElementDownABit(element);
                        }
                        document.getElementById("square").onmousedown = dragMouseDown;
                    }
                    dragElement(document.getElementById("square"));
                </script>
                </html>""";
    }
    @GetMapping("/error")
    public String error() {
        return home();
    }
    @GetMapping("/pagina-principala")
    public String paginaPrincipala(){
        return "Hello world!";
    }
}
