package rdt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;

public class SAWSender extends GBNSender
{
    protected int windowSize = 1;
    private Timer timer;

    public SAWSender(DatagramSocket socket)
    {
        super(socket);
    }

    /**
     * 超时重传
     */
    public void reSend()
    {
        if(base == (totalPkts+1)||base==nextSeqNum)
        {
            // 全部数据都已被确认，停止重传
            // 或者发送的数据均已确认，停止重传
            stopTimer();
            return;
        }

        System.err.println("****超时重传"+base+"****");
        // 重传未被确认的数据
        byte[] data = dataList.get(base);
        try
        {
            socket.send(new DatagramPacket(data,data.length));
        } catch (IOException e)
        {
            System.err.println("====数据发送失败====");
        }
        // 重启计时器
        stopTimer();
        startTimer();
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
