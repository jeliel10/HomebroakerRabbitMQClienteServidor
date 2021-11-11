package Homebroaker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Homebroaker {

    private static final String EXCHANGE_NAME = "logs";
    private static final String EXCHANGE_NAME2 = "jeliel";
    private static final String EXCHANGE_NAME3 = "mateus";


    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        System.out.println("HOMEBROAKER");
        try (Connection connection = factory.newConnection();

             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            ArrayList<String> list = new ArrayList<String>();
            list.add("1- VIVO - R$18,54\n");
            list.add("2- ABEV3 - 16,63\n");
            list.add("3- BRFS3 - R$22,56\n");
            list.add("4- KLBN11 - R$23,79\n");
            list.add("5- SUZB3 - R$48,88\n");
            byte[] result;

            for(int i = 0; i < list.size(); i++) {
                result = new byte[list.get(i).length()];
                result = list.get(i).getBytes();
                channel.basicPublish(EXCHANGE_NAME, "cadeira", null, result);
            }
            System.out.println("Ações enviadas!");
        }
        AtomicInteger cont = new AtomicInteger();
        AtomicInteger acao = new AtomicInteger();
        ConnectionFactory factory1 = new ConnectionFactory();
        factory1.setHost("localhost");
        Connection connection1 = factory1.newConnection();
        Channel channel1 = connection1.createChannel();

        channel1.exchangeDeclare(EXCHANGE_NAME2, "direct");


        channel1.queueDeclare("pano", false, false, false, null);

        channel1.queueBind("pano", EXCHANGE_NAME2, "pano");

        System.out.println("Esperando resposta do Cliente...");

        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            cont.getAndIncrement();
            String message = new String(delivery.getBody(), "UTF-8");


            if(cont.get() == 1){
                acao.set(Integer.parseInt(message));
                System.out.println("O Cliente 1 quer essa Ação: " + acao);
            }
            else if(cont.get() == 2){
                int cotas = Integer.parseInt(message);
                System.out.println("O Cliente 1 quer esse numero de Cotas: " + cotas);

                float cotasF = cotas;
                float precoFinal = 0;

                if(acao.get() == 1){
                    precoFinal = (float) (cotasF*18.54);
                }else if(acao.get() == 2){
                    precoFinal = (float) (cotasF*16.63);
                }else if(acao.get() == 3){
                    precoFinal = (float) (cotasF*22.56);
                }else if(acao.get() == 4){
                    precoFinal = (float) (cotasF*23.79);
                }else if(acao.get() == 5){
                    precoFinal = (float) (cotasF*48.88);
                }

                System.out.println("O Cliente 1 comprou Ações do Tipo "+acao+" no valor de R$"+precoFinal);
            }
        };
        channel1.basicConsume("pano", true, deliverCallback1, consumerTag -> { });

        // A partir daqui é a parte do Cliente 2
        AtomicInteger cont2 = new AtomicInteger();
        AtomicInteger acao2 = new AtomicInteger();
        ConnectionFactory factory2 = new ConnectionFactory();
        factory2.setHost("localhost");
        Connection connection2 = factory2.newConnection();
        Channel channel2 = connection2.createChannel();

        channel2.exchangeDeclare(EXCHANGE_NAME3, "direct");


        channel2.queueDeclare("cafe", false, false, false, null);

        channel2.queueBind("cafe", EXCHANGE_NAME3, "cafe");

        System.out.println("Esperando resposta do Cliente...");

        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            cont2.getAndIncrement();
            String message2 = new String(delivery.getBody(), "UTF-8");


            if(cont2.get() == 1){
                acao2.set(Integer.parseInt(message2));
                System.out.println("O Cliente 2 quer essa Ação: " + acao2);
            }
            else if(cont2.get() == 2){
                int cotas2 = Integer.parseInt(message2);
                System.out.println("O Cliente 2 quer esse numero de Cotas: " + cotas2);

                float cotasF2 = cotas2;
                float precoFinal2 = 0;

                if(acao2.get() == 1){
                    precoFinal2 = (float) (cotasF2*18.54);
                }else if(acao2.get() == 2){
                    precoFinal2 = (float) (cotasF2*16.63);
                }else if(acao2.get() == 3){
                    precoFinal2 = (float) (cotasF2*22.56);
                }else if(acao2.get() == 4){
                    precoFinal2 = (float) (cotasF2*23.79);
                }else if(acao2.get() == 5){
                    precoFinal2 = (float) (cotasF2*48.88);
                }

                System.out.println("O Cliente 2 comprou Ações do Tipo "+acao2+" no valor de R$"+precoFinal2);
            }
        };
        channel2.basicConsume("cafe", true, deliverCallback2, consumerTag -> { });

    }
}