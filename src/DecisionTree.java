import java.util.Random;

class DecisionTree extends SupervisedLearner
{
	Node root;
	Random rand = new Random();
	//Matrix decisionFeature = new Matrix();

	// need to return a int and a double so return a interiorNode
	// there is a better way to do this
	DividingColumnAndPivot pick_dividing_column_and_pivot(Matrix feature)
	{
		DividingColumnAndPivot columnAndPivot = new DividingColumnAndPivot();

		int col = rand.nextInt(feature.cols());
		int row = rand.nextInt(feature.rows());
		double pivot = feature.row(row)[col];
		columnAndPivot.column = col;
		columnAndPivot.pivot = pivot;
		return columnAndPivot;
	}


	@Override
	String name()
	{
		return null;
	}

	Node build_tree(Matrix feature, Matrix labels)
	{
		if (feature.rows() != labels.rows())
		{
			throw new RuntimeException("mismatching features and labels");
		}

		int col = pick_dividing_column_and_pivot(feature).column;
		double pivot = pick_dividing_column_and_pivot(feature).pivot;
		Matrix feat_a = new Matrix();
		Matrix feat_b = new Matrix();
		Matrix lab_a = new Matrix();
		Matrix lab_b = new Matrix();

		for (int patience = 12; patience > 0; patience--)
		{
			//checks to see if its a continous or categorical value
			int vals = feature.valueCount(col);

			//copy meta data out of the feature matrix and label matrix
			feat_a.copyMetaData(feature);
			feat_b.copyMetaData(feature);
			lab_a.copyMetaData(labels);
			lab_b.copyMetaData(labels);

			//Divide the data
			for (int i = 0; i < feature.rows(); i++)
			{
				if (vals == 0)
				{
					//continuous
					if (feature.row(i)[col] < pivot)
					{
						feat_a.takeRow(feat_a.removeRow(i));
						lab_a.takeRow(lab_a.removeRow(i));
					}
					else
					{
						feat_b.takeRow(feat_a.removeRow(i));
						lab_b.takeRow(lab_b.removeRow(i));
					}
				}
				else
				{
					//Divide on
					//categorical
					if (feature.row(i)[col] == pivot)
					{
						feat_a.takeRow(feat_a.removeRow(i));
						lab_a.takeRow(lab_a.removeRow(i));
					}
					else
					{
						feat_b.takeRow(feat_a.removeRow(i));
						lab_b.takeRow(lab_b.removeRow(i));
					}
				}
			}
			if (feat_a.rows() != 0 || feat_b.rows() != 0)
			{
				break;
			}
		}
		//failed to divide the data then return a leaf node
		if (feat_a.rows() == 0 || feat_b.rows() == 0)
		{
			//assumes leaf node constructor just computes mean of label values and
			return new LeafNode(labels);
		}

		//Make the node
		Node nodeA = build_tree(feat_a, lab_a);
		Node nodeB = build_tree(feat_b, lab_b);
		return new InteriorNode(nodeA, nodeB, col, pivot);
	}

	@Override
	void train(Matrix features, Matrix labels)
	{
		root = build_tree(features, labels);
		//decisionFeature = new Matrix(features);
	}

	@Override
	void predict(double[] in, double[] out)
	{
		Node n = root;
		while (true)
		{
			if (n.isInterior())
			{
				if (in[((InteriorNode)n).attribute] < ((InteriorNode)n).pivot)
				{
					n = ((InteriorNode)n).a;
				}
				else
				{
					n = ((InteriorNode)n).b;
				}
			}
			else
			{
				 out = ((LeafNode)n).label;

			}
		}
	}
}
