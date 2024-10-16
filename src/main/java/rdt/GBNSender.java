package rdt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

public class GBNSender implements Sender
{
    private final DatagramSocket socket;
    private String fileName;
    private int totalPkts;
    private final int windowSize = 5; // 窗口大小
    private int base;
    private int nextSeqNum;
    private Timer timer;
    private final int TIMEOUT = 100; // 超时时间为0.1s
    private final List<byte[]> dataList = new ArrayList<>();
    private final Random random = new Random();

    public GBNSender(DatagramSocket socket)
    {
        this.socket = socket;
    }

    @Override
    public void sendFile(String fileName, int pktNum)
    {
        this.fileName = fileName;
        this.totalPkts = pktNum;
        this.base = 0;
        this.nextSeqNum = 0;

        String message = "-createFile "+fileName+" "+pktNum;
        byte[] buffer = message.getBytes();
        try
        {
            socket.send(new DatagramPacket(buffer, buffer.length));
        } catch (IOException e)
        {
            System.out.println("====创建文件请求发送失败====");
        }
    }

    @Override
    public void sendData(byte[] data)
    {
        // 首先需要存储并发送数据
        dataList.add(data);
        // 生成一个随机数，模拟丢包
        if (random.nextDouble() >= 0.2) { // 80%的概率发送
            try {
                socket.send(new DatagramPacket(data, data.length));
                System.out.println("====数据包:"+nextSeqNum+"已发送====");
            } catch (IOException e) {
                System.err.println("====数据发送失败====");
            }
        } else {
            System.err.println("****丢包:"+nextSeqNum+"****");
        }
        // 要么是开始时，要么是所有发送的数据都被确认了
        // 此时应当重启计时器
        if(base == nextSeqNum)
        {
            stopTimer();
            startTimer();
        }
        nextSeqNum++;
    }

    @Override
    public void receiveACK(byte[] ack)
    {
        int seqNum = new GBN().getSeqnum(ack);
        System.out.println("====ACK:"+seqNum+"====");
        if(seqNum == totalPkts)
        {
            System.out.println("====收到所有ACK,文件传输完成====");
        }
        base = seqNum + 1;
        if(nextSeqNum == base)
        {
         // 当发送的数据全部接收时，停止计时器
         stopTimer();
        }
        else
        {
         // 否则一旦有数据被确认，就重启计时器
         stopTimer();
         startTimer();
        }
    }

    @Override
    public boolean isFull()
    {
        return (nextSeqNum-base)==windowSize;
    }

    /**
     * 超时重传
     */
    public void reSend()
    {
        if(base == (totalPkts+1))
        {
            // 全部数据都已被确认，停止重传
            stopTimer();
            return;
        }

        System.err.println("****超时重传"+base+"到"+(nextSeqNum-1)+"****");
        // 重传所有未被确认的数据
        for(int i=base;i<nextSeqNum;i++)
        {
            byte[] data = dataList.get(i);
            try
            {
                socket.send(new DatagramPacket(data,data.length));
            } catch (IOException e)
            {
                System.err.println("====数据发送失败====");
            }
        }
    }

    private void startTimer()
    {
        Timer t = new Timer();
        t.schedule(new GBNTimerTask(this),TIMEOUT);
        timer = t;
    }

    private void stopTimer()
    {
        if(timer!= null)
        {
            timer.cancel();
        }
        timer = null;
    }
}
