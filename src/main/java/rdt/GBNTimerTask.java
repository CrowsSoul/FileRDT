package rdt;

import java.util.TimerTask;

/**
 * GBNSender类使用的计时器任务
 * 超时后触发重传
 */
public class GBNTimerTask extends TimerTask
{
    private final GBNSender sender;

    public GBNTimerTask(GBNSender sender)
    {
        this.sender = sender;
    }

    @Override
    public void run()
    {
        sender.reSend();
    }
}
