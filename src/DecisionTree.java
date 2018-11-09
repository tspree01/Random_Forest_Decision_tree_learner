import java.util.Random;

class DecisionTree extends SupervisedLearner
{
	Node root;
	Random rand = new Random();
	Matrix decisionFeature = new Matrix();

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
		return "Decision Tree";
	}

	Node build_tree(Matrix feature, Matrix labels)
	{
		Matrix feat_a = new Matrix();
		Matrix feat_b = new Matrix();
		Matrix lab_a = new Matrix();
		Matrix lab_b = new Matrix();

		if (feature.rows() != labels.rows())
		{
			throw new RuntimeException("mismatching features and labels");
		}

		int col = 0;
		double pivot = 0;
		int featureRows = 0;

		for (int patience = 12; patience > 0; patience--)
		{
			DividingColumnAndPivot dcp = pick_dividing_column_and_pivot(feature);
			col = dcp.column;
			pivot = dcp.pivot;

			//Make a copy of the feature and labels to use to see if you get a good split
			Matrix copyFeature = new Matrix(feature);
			Matrix copyLabels = new Matrix(labels);

			//checks to see if its a continous or categorical value
			int vals = feature.valueCount(col);

			//copy meta data out of the feature matrix and label matrix
			feat_a = new Matrix();
			feat_b = new Matrix();
			lab_a = new Matrix();
			lab_b = new Matrix();
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
					if (copyFeature.row(0)[col] < pivot)
					{
						feat_a.takeRow(copyFeature.removeRow(0));
						lab_a.takeRow(copyLabels.removeRow(0));
					}
					else
					{
						feat_b.takeRow(copyFeature.removeRow(0));
						lab_b.takeRow(copyLabels.removeRow(0));
					}
				}
				else
				{
					//Divide on
					//categorical
					if (feature.row(0)[col] == pivot)
					{
						feat_a.takeRow(copyFeature.removeRow(0));
						lab_a.takeRow(copyLabels.removeRow(0));
					}
					else
					{
						feat_b.takeRow(copyFeature.removeRow(0));
						lab_b.takeRow(copyLabels.removeRow(0));
					}
				}
			}
			if (feat_a.rows() != 0 && feat_b.rows() != 0)
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
		decisionFeature = new Matrix(features);
	}

	@Override
	void predict(double[] in, double[] out)
	{
		Node n = root;
		while (!n.isLeaf())
		{
			if (n.isInterior())
			{
				int vals = decisionFeature.valueCount(((InteriorNode)n).attribute);
				if (vals == 0)
				{
					if (in[((InteriorNode) n).attribute] < ((InteriorNode) n).pivot)
					{
						n = ((InteriorNode) n).a;
					}
					else
					{
						n = ((InteriorNode) n).b;
					}
				}
				else{
					if (in[((InteriorNode) n).attribute] == ((InteriorNode) n).pivot)
					{
						n = ((InteriorNode) n).a;
					}
					else
					{
						n = ((InteriorNode) n).b;
					}
				}
			}
		}
		Vec.copy(out,((LeafNode)n).label);
	}
}
