package com.netty.client;

import com.netty.model.User;
import com.netty.model.UserContact;

import java.util.Scanner;

public class BusinessLogicClient {
    public static void main(String[] args) throws Exception {
        ClientStarter clientStarter = new ClientStarter();
        new Thread(clientStarter).start();
        while(!clientStarter.isConnected()){
            synchronized (clientStarter){
                clientStarter.wait();
            }
        }
        System.out.println("Start........");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.next();
            if (msg == null) {
                break;
            } else if ("q".equals(msg.toLowerCase())) {
                clientStarter.close();
                scanner.close();
                while(clientStarter.isConnected()){
                    synchronized (clientStarter){
                        System.out.println("Close connection....");
                        clientStarter.wait();
                    }
                }
                System.exit(1);
            } else if("v".equals(msg.toLowerCase())){
                User user = new User();
                user.setAge(19);
                String userName = "mark";
                user.setUserName(userName);
                user.setId("No:1");
                user.setUserContact(
                        new UserContact(userName+"@tuling.com",
                                "133"));
                clientStarter.send(user);
            }else if("o".equals(msg.toLowerCase())){
                User user = new User();
                user.setAge(23);
                String userName = "oneway";
                user.setUserName(userName);
                user.setId("No:1");
                user.setUserContact(
                        new UserContact(userName+"@tuling.com",
                                "331"));
                clientStarter.sendOneWay(user);
            }
            else {
                clientStarter.send(msg);
            }
        }
    }
}
