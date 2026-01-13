package com.jintu.jintuaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;


import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-06
 * @Description:基于文件的聊天记录
 * @Version: 1.0
 */
public class FileBasedChatMemory implements ChatMemory {
    private final String BASE_DIR;
    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired( false);
        //设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    //构造对象，指定文件保存目录
    public FileBasedChatMemory(String baseDir) {
        this.BASE_DIR = baseDir;
        File file = new File(baseDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessage=getOrCreateConversation(conversationId);
        conversationMessage.addAll(messages);
        saveConversation(conversationId,conversationMessage);
    }

    @Override
    public List<Message> get(String conversationId) {
        return getOrCreateConversation(conversationId);
    }


    @Override
    public void clear(String conversationId) {
        File file=getConversationFile(conversationId);
        if(file.exists()){
            file.delete();
        }
    }

    private List<Message> getOrCreateConversation(String conversationId){
        File file=getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if(file.exists()){
            try(Input input = new Input(new FileInputStream( file)))
            {
                messages=kryo.readObject(input,ArrayList.class);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return messages;
    }


    private void saveConversation(String conversationId,List<Message> messages){
        File file=getConversationFile(conversationId);
        try{
            file.createNewFile();
            try(Output output = new Output(new FileOutputStream(file)))
            {
                kryo.writeObject(output,messages);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private File getConversationFile(String conversationId){
        return new File(BASE_DIR,conversationId+".kryo");
    }
}
