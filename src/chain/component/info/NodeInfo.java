package chain.component.info;

/**
 * 
 * 	Object that contain all info about a node of chain
 * 
 * 
 * @author giorg
 *
 */
public class NodeInfo {

	// index of node
	private int nodeIndex;
	
	// Host and port of node
	private String nodeHostName;
	private int nodePort;

	// It's start at 0. It can increment or decrement.
	private long confidence;

	
	
	// TODO: add other info about a single node



	public NodeInfo() {
	}

	public NodeInfo(int nodeIndex, String nodeHostName, int nodePort) {
		super();
		this.nodeIndex = nodeIndex;
		this.nodeHostName = nodeHostName;
		this.nodePort = nodePort;
	}









	public int getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public String getNodeHostName() {
		return nodeHostName;
	}


	public void setNodeHostName(String nodeHostName) {
		this.nodeHostName = nodeHostName;
	}


	public int getNodePort() {
		return nodePort;
	}


	public void setNodePort(int nodePort) {
		this.nodePort = nodePort;
	}


	public long getConfidence() {
		return confidence;
	}


	public void setConfidence(long confidence) {
		this.confidence = confidence;
	}

	
	
	
	@Override
	public String toString() {
		return "NodeInfo [nodeIndex=" + nodeIndex + ", nodeHostName=" + nodeHostName + ", nodePort=" + nodePort
				+ ", confidence=" + confidence + "]";
	}
	
}