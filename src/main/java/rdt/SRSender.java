package rdt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class SRSender extends GBNSender
{
    private Map<Integer,Timer> timers;
    private Map<Integer,Boolean> acks;

    public SRSender(DatagramSocket socket)
    {
        super(socket);
    }

    @Override
    public void sendFile(String fileName,int pktNum)
    {
        super.sendFile(fileName,pktNum);
        timers = new HashMap<>();
        acks = new HashMap<>();
        // 初始化所有数据包的ACK状态为未确认
        for(int i=0;i<=totalPkts;i++)
        {
            acks.put(i,false);
        }
    }

    @Override
    public void sendData(byte[] data)
    {
        // 首先需要存储并发送数据
        dataList.add(data);
        // 生成一个随机数，模拟丢包
        if (random.nextDouble() >= 0.2)
        { // 80%的概率发送
            try {
                startTimer(new SR().getSeqnum(data));
                socket.send(new DatagramPacket(data, data.length));
                System.out.println("====数据包:"+nextSeqNum+"已发送====");
            } catch (IOException e) {
                System.err.println("====数据发送失败====");
            }
        } else {
            startTimer(new SR().getSeqnum(data));
            System.err.println("****丢包:"+nextSeqNum+"****");
        }
        nextSeqNum++;
    }

    @Override
    public void receiveACK(byte[] ack)
    {
        int seqNum = new SR().getSeqnum(ack);
        // 停止定时器
        stopTimer(seqNum);
        // 将ACK设置为已确认
        acks.put(seqNum,true);

        System.out.println("====ACK:"+seqNum+"====");
        // 当base等于seqNum时，检查连续的已确认的ack
        if(base == seqNum && base <= totalPkts)
        {
            System.out.println("====发送方窗口移动!====");
            System.out.printf("====发送方原窗口[%d,%d,%d]====\n",base,Math.min(nextSeqNum,totalPkts),Math.min(nextSeqNum+windowSize-1,totalPkts));
            int end = base;
            // 主要保证end不要超过totalPkts
            while(end<=totalPkts && acks.get(end))
            {
                end++;
            }
            base = end;
            if(base == totalPkts+1)
            {
                // 全部数据都已被确认，停止发送
                System.out.println("====接收到所有数据包的ACK，窗口移动到末尾!====");
                System.out.printf("====文件传输完成!====");
                return;
            }
            System.out.printf("====发送方现窗口[%d,%d,%d]====\n",base,nextSeqNum,Math.min(nextSeqNum+windowSize-1,totalPkts));
        }

    }

    private void startTimer(int seqNum)
    {
        Timer t = new Timer();
        t.schedule(new SRTimerTask(seqNum,this),TIMEOUT);
        timers.put(seqNum,t);
    }

    private void stopTimer(int seqNum)
    {
        Timer t = timers.get(seqNum);
        if(t!= null)
        {
            t.cancel();
        }
    }

    public void reSend(int seqNum)
    {
        if(base == (totalPkts+1)||base==nextSeqNum)
        {
            // 全部数据都已被确认，停止重传
            // 或者发送的数据均已确认，停止重传
            stopTimer(seqNum);
            return;
        }
        System.err.println("****超时重传:"+seqNum+"****");
        byte[] data = dataList.get(seqNum);
        try
        {
            socket.send(new DatagramPacket(data,data.length));
            // 重发后，重新启动定时器
            stopTimer(seqNum);
            startTimer(seqNum);

        } catch (IOException e)
        {
            System.err.println("====数据发送失败====");
        }
    }

}
