package cs;

import rdt.RDT;
import rdt.Sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileClient implements Runnable
{
    private final RDT rdt;
    private final BufferedReader reader;
    private final Sender sender;
    private final List<byte[]> pkts = new ArrayList<>(); //存储数据包
    private String fileName;

    public FileClient(BufferedReader reader, Sender sender, RDT rdt)
    {
        this.reader = reader;
        this.sender = sender;
        this.rdt = rdt;
    }

    @Override
    public void run()
    {
        System.out.println("====FileClient Started====");
        System.out.println("====输入exit将退出程序!====");
        String input = "";
        while(true)
        {
            try {
                input = reader.readLine();
            } catch (IOException ignored){}
            // 输入exit退出程序
            if(input.equals("exit"))
            {
                break;
            }
            if(isCommand(input))
            {
                readFile(getFileName(input));
                sender.sendFile(fileName,pkts.size());
                for(byte[] pkt:pkts)
                {
                    while(sender.isFull())
                    {
                        try
                        {
                            Thread.sleep(100);// 等待直到窗口空闲
                        } catch (InterruptedException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                    sender.sendData(pkt);
                }
                System.out.println("==== 文件:"+fileName+" 发送完毕！====");
            }
        }
        System.out.println("====FileClient Exited====");
    }

    //根据fileName读取文件内容，划分为多个数据包，并存储
    private void readFile(String filename)
    {

    }

    //判断是否为命令
    private boolean isCommand(String input)
    {
        return false;
    }

    // 处理命令 提取出文件名
    private String getFileName(String input)
    {
        return null;
    }
}
