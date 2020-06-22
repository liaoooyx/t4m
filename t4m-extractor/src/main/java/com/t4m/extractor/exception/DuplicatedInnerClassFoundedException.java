package com.t4m.extractor.exception;

/**
 * Created by Yuxiang Liao on 2020-06-21 21:18.
 */

/**
 * 运行时异常，通常不会发生。只有在不得不传入简单内部类名时才有可能抛出，这时候需要在Visitor中捕获异常并记录日志。
 * @author Yuxiang
 */
public class DuplicatedInnerClassFoundedException extends RuntimeException{

	public DuplicatedInnerClassFoundedException(String message){
		super("在当前类文件所在的包，和引入的包中，存在多个同名内部类（解析时只能通过$后的内部类名进行匹配）， which are:" + message);
	}

}
