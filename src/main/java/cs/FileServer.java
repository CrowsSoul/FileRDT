package cs;

import rdt.RDT;
import rdt.Receiver;
import rdt.Sender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileServer implements Runnable
{
    private final RDT rdt;
    private final Sender sender;
    private final Receiver receiver;
    private final DatagramSocket socket;
    private final List<byte[]> fileData = new ArrayList<>(); // 文件数据
    private int totalPkts = 0; // 分组数
    private String filename = ""; // 文件名
    private final String fileDir;

    public FileServer(RDT rdt, Sender sender, Receiver receiver, DatagramSocket socket,String fileDir)
    {
        this.rdt = rdt;
        this.sender = sender;
        this.receiver = receiver;
        this.socket = socket;
        this.fileDir = fileDir;
    }

    @Override
    public void run()
    {
        System.out.println("====FileServer started====");
        while (!Thread.currentThread().isInterrupted())
        {
            // 数据缓冲区:
            byte[] buffer = new byte[1024];
            byte[] actualPkt = new byte[0];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet); // 收取一个UDP数据包
                // 创建一个长度恰好的byte[]，并复制实际接收到的数据
                actualPkt = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
            } catch (IOException ignored) {}
            // 若没有收到任何数据，则继续等待
            if(actualPkt.length == 0)
            {
                continue;
            }
            if(rdt.isACK(actualPkt))
            {
                // 若是ACK，则交给Sender处理
                sender.receiveACK(actualPkt);
            }
            else
            {
                if(isCommand(actualPkt))
                {
                    // 若是命令，则需新建文件并初始化
                    filename = getFilename(actualPkt);
                    totalPkts = getPktNum(actualPkt);
                    createFile(filename,totalPkts);
                }
                else
                {
                    int seqNum = rdt.getSeqnum(actualPkt);
                    // 若是数据，则交给Receiver处理
                    receiver.receiveData(actualPkt);
                    // 需要在List中添加
                    fileData.set(seqNum, rdt.getData(actualPkt));
                    if(rdt.getSeqnum(actualPkt) == totalPkts)
                    {
                        // 若是最后一组数据,则应将所有数据写入文件
                        System.out.println("====文件:"+filename+"接收完成====");
                        writeFile();
                    }
                }
            }
        }
        System.out.println("====Server stopped====");

    }


    // 用于创建文件且存储基本信息。还要初始化Receiver
    private void createFile(String filename,int pktNum)
    {
        this.totalPkts = pktNum;
        String filePath = fileDir + filename;
        for (int i = 0; i <= pktNum; i++)
        {
            fileData.add(new byte[1024]); // 初始化fileData列表
        }
        // 创建文件对象
        File file = new File(filePath);
        try {
            // 创建文件，如果文件不存在，则创建新文件
            if (file.createNewFile())
            {
                System.out.println("====文件已创建: " + filePath + "====");
                System.out.println("====需要接收数据包的个数: " + (pktNum+1) + "====");
            } else
            {
                System.out.println("====文件已存在: " + filePath +"====");
            }
        } catch (IOException e) {
            System.err.println("创建文件失败: " + e.getMessage());
        }
        // 初始化Receiver
        receiver.receiveFile(filename,pktNum);
    }

    // 用于接收到全部分组后，将其写入文件
    private void writeFile()
    {
        String filePath = fileDir + filename;
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // 遍历 fileData 列表中的每个数据包
            for (byte[] data : fileData)
            {
                fos.write(data); // 将数据包内容写入文件
            }
            System.out.println("====文件写入完成: " + filePath +"====");
        } catch (IOException e) {
            System.err.println("文件写入失败: " + e.getMessage());
        }

    }

    // 用于判断接收到的数据是否是命令
    private boolean isCommand(byte[] pkt)
    {
        String cmd = new String(pkt);
        return cmd.startsWith("-createFile");
    }

    // 用于获取文件名
    private String getFilename(byte[] pkt)
    {
        String cmd = new String(pkt);
        return cmd.split(" ")[1];
    }

    // 用于获取分组数
    private int getPktNum(byte[] pkt)
    {
        String cmd = new String(pkt);
        return Integer.parseInt(cmd.split(" ")[2]);
    }
}
