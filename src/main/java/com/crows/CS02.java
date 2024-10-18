package com.crows;

import cs.FileClient;
import cs.FileServer;
import rdt.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class CS02 {
    private final static int PORT = 10002;
    private final static int TARGET_PORT = 10001;
    private static DatagramSocket socket;
    private static Sender sender;
    private static Receiver receiver;
    private static RDT rdt;

    public static void main(String[] args)
    {
        try(DatagramSocket ds = new DatagramSocket(PORT);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {
            // 设置目的地址
            ds.connect(InetAddress.getByName("localhost"), TARGET_PORT);
            socket = ds;
            rdt = new SR();
            sender = rdt.createSender(socket);
            receiver = rdt.createReceiver(socket);

            // 启动客户端和服务端线程
            Thread clientThread = new Thread(new FileClient(br,sender,rdt,"src/main/resources/CS02/"));
            Thread serverThread = new Thread(new FileServer(rdt,sender,receiver,socket,"src/main/resources/CS02/"));
            clientThread.start();
            serverThread.start();

            clientThread.join(); // 等待clientThread结束
            serverThread.interrupt(); // 向serverThread发送中断请求
        }
        catch(SocketException e)
        {
            System.out.println("Socket creation failed");
        }
        catch(IOException e)
        {
            System.out.println("I/O error occurred");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}