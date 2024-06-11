# Spring AI Function Calling Sample Application

## Run

```sh
export SPRING_AI_OPENAI_API_KEY="your-openai-api-key"
./mvnw spring-boot:run
```

## Example

This example call a function that checks if the manager is available.

```sh
curl http://localhost:8080\?msg\="Is+my+manager+available?"
Your manager is currently busy.
```

If no need to call a function, the application will respond with a generic message.

```sh
curl http://localhost:8080\?msg\="Can+you+help+my+programming+task?"
Of course! I'd be happy to help with your programming task. Please provide me with more details about the task and let me know how I can assist you.
```