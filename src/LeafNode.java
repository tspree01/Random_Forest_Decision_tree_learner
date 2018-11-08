class LeafNode extends Node
{
	double[] label;

	@Override
	boolean isInterior()
	{
		return false;
	}

	boolean isLeaf() { return true; }

	LeafNode(Matrix labels){
		label = new double[labels.cols()];
		for(int i = 0; i < labels.cols(); i++)
		{
			if(labels.valueCount(i) == 0)
				label[i] = labels.columnMean(i);
			else
				label[i] = labels.mostCommonValue(i);
		}
	}
}