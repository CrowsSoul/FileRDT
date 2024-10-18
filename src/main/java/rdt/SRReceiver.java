package rdt;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class SRReceiver extends GBNReceiver
{
    private Map<Integer,Boolean> acks;
    private final int windowSize = 5;//滑动窗口大小

    public SRReceiver(DatagramSocket socket)
    {
        super(socket);
    }

    @Override
    public void receiveFile(String fileName,int pktNum)
    {
        super.receiveFile(fileName,pktNum);
        acks = new HashMap<>();
        // 初始化acks全为未确认
        for(int i=0;i<=pktNum;i++)
        {
            acks.put(i,false);
        }
    }

    @Override
    public void receiveData(byte[] data)
    {
        int seqNum = new GBN().getSeqnum(data);
        System.out.println("====收到数据包:"+seqNum+"====");
        // 确认接收数据包
        if(seqNum>=expectedSeqNum-windowSize && seqNum<=expectedSeqNum+windowSize-1 && expectedSeqNum<=totalPkts)
        {
            acks.put(seqNum,true);
            sendACK(seqNum);
            if(seqNum==expectedSeqNum)
            {
                // 此时需要移动窗口
                System.out.println("====接收方窗口移动!====");
                System.out.printf("====接收方原窗口[%d,%d]====\n",expectedSeqNum,
                        Math.min(expectedSeqNum + windowSize - 1, totalPkts));
                int end = expectedSeqNum;
                // 主要保证end不要超过totalPkts
                while(end<=totalPkts && acks.get(end))
                {
                    end++;
                }
                expectedSeqNum = end;
                // 这里已经接收完毕
                if(expectedSeqNum==totalPkts+1)
                {
                    System.out.println("====接收到所有数据包，窗口移动到末尾!====");
                    return;
                }
                System.out.printf("====接收方现窗口[%d,%d]====\n",expectedSeqNum,
                        Math.min(expectedSeqNum + windowSize - 1, totalPkts));
            }
        }
    }

    @Override
    public boolean isFinished()
    {
        if(expectedSeqNum==totalPkts+1)
        {
            System.out.println("====所有数据包均接收完毕！====");
            return true;
        }
        return false;
    }
}
