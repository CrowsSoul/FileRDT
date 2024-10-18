package cs;

import rdt.RDT;
import rdt.Sender;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
    private final String fileDir;

    public FileClient(BufferedReader reader, Sender sender, RDT rdt,String fileDir)
    {
        this.reader = reader;
        this.sender = sender;
        this.rdt = rdt;
        this.fileDir = fileDir;
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
                fileName = getFileName(input);
                readFile();
                System.out.println("==== 文件:"+fileName+" 开始发送！====");
                sender.sendFile(fileName,pkts.size()-1);
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
    private void readFile()
    {
        String filePath = fileDir + fileName;
        int seqNum = 0; // 序号
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1020]; // 每次最多读取1020字节
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                // 调用 addSeqnum 并生成新的数据包
                byte[] packet = rdt.addSeqnum(buffer, seqNum, bytesRead);

                // 将生成的packet添加到列表中
                pkts.add(packet);

                // 增加序号
                seqNum++;
            }
            System.out.println("==== 已生成数据包个数:"+seqNum+"====");
        } catch (IOException e) {
            System.err.println("====读取文件失败!====");
        }
    }

    //判断是否为命令
    private boolean isCommand(String input)
    {
        return input.startsWith("-sendFile");
    }

    // 处理命令 提取出文件名
    private String getFileName(String input)
    {
        String[] arr = input.split(" ");
        return arr[1];
    }
}
