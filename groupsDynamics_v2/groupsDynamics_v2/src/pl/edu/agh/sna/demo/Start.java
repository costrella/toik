package pl.edu.agh.sna.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Start {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"beans.xml");
		
		GroupDynamicsAnalyzerDemo demo = (GroupDynamicsAnalyzerDemo) context.getBean("groupDynamics");
		demo.analyzeExampleWithTopicContext();
	}

}
