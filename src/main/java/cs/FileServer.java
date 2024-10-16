package cs;

import rdt.RDT;
import rdt.Receiver;
import rdt.Sender;

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

    public FileServer(RDT rdt, Sender sender, Receiver receiver, DatagramSocket socket)
    {
        this.rdt = rdt;
        this.sender = sender;
        this.receiver = receiver;
        this.socket = socket;
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
                    createFile(getFilename(actualPkt),getPktNum(actualPkt));
                }
                else
                {
                    // 若是数据，则交给Receiver处理
                    receiver.receiveData(actualPkt);
                    // 需要在List中添加
                    fileData.add(rdt.getData(actualPkt));
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

    }

    // 用于接收到全部分组后，将其写入文件
    private void writeFile()
    {

    }

    // 用于判断接收到的数据是否是命令
    private boolean isCommand(byte[] pkt)
    {
        return false;
    }

    // 用于获取文件名
    private String getFilename(byte[] pkt)
    {
        return null;
    }

    // 用于获取分组数
    private int getPktNum(byte[] pkt)
    {
        return 0;
    }
}
