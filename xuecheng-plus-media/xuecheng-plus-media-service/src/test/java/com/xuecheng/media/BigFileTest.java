package com.xuecheng.media;

import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 大文件处理
 */
public class BigFileTest {

    //建立连接
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void testChunk() throws Exception{
        //分块文件路径
        File sourceFile = new File("D:\\Desktop\\tvideo.mp4");
        String chunkPath = "D:\\Desktop\\JAVA\\test\\";
        File chunkFolder = new File(chunkPath);
        if(!chunkFolder.exists()){
            chunkFolder.mkdirs();
        }
        //分块
        long chunkSize = 1024 * 1024 * 5;
        //分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println("分块总数:"+chunkNum);
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用randomAccessFile访问文件
        RandomAccessFile r = new RandomAccessFile(sourceFile, "r");
        //分块
        for(int i = 0;i<chunkNum;i++){
            //创建分块文件
            File file = new File(chunkPath+i);
            if(!file.exists()){
                file.delete();
            }
            boolean newFile = file.createNewFile();
            //创建成功
            if(newFile){
                //写入数据
                RandomAccessFile rw = new RandomAccessFile(file, "rw");
                int len = -1;
                while((len = r.read(b))!= -1){
                    rw.write(b, 0, len);
                    //大于分块大小，说明已满
                    if(file.length() >= chunkSize){
                        break;
                    }
                }
                rw.close();
                System.out.println("完成分块"+i);
            }
        }
        r.close();
    }

    //测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        //块目录
        File chunkFolder = new File("D:\\Desktop\\JAVA\\test");
        //原始文件
        File originalFile = new File("D:\\Desktop\\tvideo.mp4");
        //合并文件
        File mergeFile = new File("D:\\Desktop\\JAVA\\tvideo1.mp4");
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //拿到所有分块
        File[] files = chunkFolder.listFiles();
        //转成集合，便于排序
        List<File> fileList = Arrays.asList(files);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for(File file:fileList){
            RandomAccessFile r = new RandomAccessFile(file, "r");
            int len = -1;
            while((len = r.read(b))!=-1){
                raf_write.write(b, 0, len);
            }
            r.close();
        }
        raf_write.close();

        //校验文件
        try (
            FileInputStream fileInputStream = new FileInputStream(originalFile);
            FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }
        }
    }

    //将分块文件上传至minio
    @Test
    public void uploadChunk(){
        //块目录
        File chunkFolder = new File("D:\\Desktop\\JAVA\\test");
        //拿到所有分块
        File[] files = chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            try {
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket("testbucket")
                        .object("chunk/" + i)
                        .filename(files[i].getAbsolutePath())
                        .build();
                minioClient.uploadObject(uploadObjectArgs);
                System.out.println("上传分块成功" + i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //合并文件，要求分块文件最小5M
    @Test
    public void test_merge() throws Exception {
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(3)
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunk/".concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());

        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("tvideo1.mp4")
                .sources(sources)
                .build();
        minioClient.composeObject(composeObjectArgs);
    }

    //清除分块文件
    @Test
    public void test_removeObjects(){
        //合并分块完成将分块文件清除
        List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                .limit(6)
                .map(i -> new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("testbucket")
                .objects(deleteObjects)
                .build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError = null;
            try {
                deleteError = r.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
