package Cliente02;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Cliente02 {
    private static final String EXCHANGE_NAME = "logs";
    private static final String EXCHANGE_NAME3 = "mateus";

    public static void main(String[] argv) throws Exception {

        ArrayList<String> list = new ArrayList<String>();
        Scanner sc = new Scanner(System.in);
        AtomicInteger cont = new AtomicInteger();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        channel.queueDeclare("tomada", false, false, false, null);

        channel.queueBind("tomada", EXCHANGE_NAME, "cadeira");

        System.out.println("Cliente 2");
        System.out.println("Esperando recebimento de Ações");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String msg = new String(delivery.getBody(), "UTF-8");
            list.add(msg);
            System.out.println(msg);
            cont.getAndIncrement();

            if(cont.get() == 5) {
                System.out.println("Essas são as Ações disponiveis");
                Channel channel2 = connection.createChannel();{

                    channel2.exchangeDeclare(EXCHANGE_NAME3, "direct");

                    System.out.println("Quais ações o senhor deseja comprar, digite o numero equivalente a ela: ");
                    String respost = sc.nextLine();

                    channel2.basicPublish(EXCHANGE_NAME3, "cafe", null, respost.getBytes("UTF-8"));
                }
                Channel channel3 = connection.createChannel();
                {

                    channel3.exchangeDeclare(EXCHANGE_NAME3, "direct");

                    System.out.println("Quantas cotas dessa Ação: ");
                    int cotas = sc.nextInt();
                    String cotasStr = String.valueOf(cotas);

                    channel3.basicPublish(EXCHANGE_NAME3, "cafe", null, cotasStr.getBytes("UTF-8"));
                }
            }
        };
        channel.basicConsume("tomada", true, deliverCallback, consumerTag -> { });
    }
}