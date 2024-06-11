package com.example.springaifunctions;


import java.util.function.Function;


public class MyManagerStatusService implements Function<MyManagerStatusService.Request, MyManagerStatusService.Response> {
    private final String[] statuses = {"Free", "Busy", "On Break"};

    @Override
    public Response apply(Request request) {
        int randomIndex = (int) (Math.random() * statuses.length);
        String status = statuses[randomIndex];
        return new Response(status);
    }

    public record Request(String msg) {}
    public record Response(String status) {}
}