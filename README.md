# Simple Message Passing Java Client

Implementation of a client for connecting to a [Simple Message Passing Server](https://github.com/scruz84/smp-server).

This client follows a NIO implementation. 

Declare next dependency:

``` xml
<dependency>
  <groupId>io.github.scruz84</groupId>
  <artifactId>smp_client</artifactId>
  <version>0.1.1</version>
</dependency>
```

Example:

``` java
final Client client = new ClientBuilder()
    .setHost("localhost")
    .setPort(1984)
    .setUser("sergio")
    .setPassword("secret")
    .setOnMessageListener(new MyMessageListener())
    .build();

client.subscribeTopic("my-topic");
client.unSubscribeTopic("my-topic");
```

Listener example:
``` java
private static class MyMessageListener implements Client.OnMessageListener {

    @Override
    public void onMessage(String topic, byte[] message) {
        System.out.println("Received from topic " + topic + ", content " + new String(message));
    }

    @Override
    public void onError(Throwable t) {
        logger.error("Error received!. " + t.getMessage(), t);
    }
}
```