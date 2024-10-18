package rdt;

import java.util.TimerTask;

public class SRTimerTask extends TimerTask
{
    private final int SeqNum;
    private final SRSender sender;

    public SRTimerTask(int seqNum, SRSender sender)
    {
        this.SeqNum = seqNum;
        this.sender = sender;
    }

    @Override
    public void run()

    {
        sender.reSend(SeqNum);
    }
}
