package org.jsirenia.file;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.jsirenia.bean.PathUtil;
import org.jsirenia.util.callback.Callback10;

public class FileWatcher {
	private String dir;
	private List<Kind<Path>> kindList = new ArrayList<>();
	public FileWatcher withDir(String dir){
		this.dir = dir;
		return this;
	}
	/**
	 * 参考StandardWatchEventKinds
	 * @param kind
	 * @return
	 */
	public FileWatcher withKind(Kind<Path> kind){
		kindList.add(kind);
		return this;
	}
	/**
	 * 参考WatchEvent
	 * @param cb
	 */
	public void watch(Callback10<WatchEvent<?>> cb){
		try{
			watchInternal(cb);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	public void watchInternal(Callback10<WatchEvent<?>> cb) throws IOException, InterruptedException{
		WatchService watcher = FileSystems.getDefault().newWatchService();
		Path path = FileSystems.getDefault().getPath(dir);
		//WatchKey key = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
		WatchKey key = path.register(watcher, kindList.toArray(new Kind[kindList.size()]));
		while (true) {  
            key = watcher.take();  
            for (WatchEvent<?> event : key.pollEvents()) { 
               /* //获取文件名
                String fileName = event.context().toString();
                //检查文件名是否符合要求
                if("test.txt".equals(fileName)){
                    String filePath = path.toFile().getAbsolutePath()+File.separator+fileName;
                    System.out.println(new JFile(filePath).text());
                }*/
            	cb.apply(event);
            }
            key.reset();  
        }  
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		String dir = "f:/";
		new FileWatcher().withDir(dir).withKind(StandardWatchEventKinds.ENTRY_MODIFY)
		.watch(event->{
			//获取目录下新增的文件名
            String fileName = event.context().toString();
            //检查文件名是否符合要求
            if("text.txt".equals(fileName)){
                String filePath = PathUtil.concat(dir, fileName);
                System.out.println(new JFile(filePath).text());
            }
		});
	}
}
