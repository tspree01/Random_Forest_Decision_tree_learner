import java.util.ArrayList;
import java.util.Random;

class RandomForest extends SupervisedLearner
{
	DecisionTree[] trees;
	Random rand = new Random();

	RandomForest(int numberOfTrees){
		trees = new DecisionTree[numberOfTrees];
	}

	@Override
	String name()
	{
		return "Random Forest";
	}

	@Override
	void train(Matrix features, Matrix labels)
	{
		Matrix baggedFeatures = new Matrix();
		Matrix baggedLabels = new Matrix();

		baggedFeatures.copyMetaData(features);
		baggedLabels.copyMetaData(labels);

		for (int i = 0; i < trees.length; i++)
		{
			baggedFeatures = new Matrix();
			baggedLabels = new Matrix();
			baggedFeatures.copyMetaData(features);
			baggedLabels.copyMetaData(labels);

			for (int j = 0; j < features.rows(); j++)
			{
				int row = rand.nextInt(features.rows());
				baggedFeatures.takeRow(features.row(row));
				baggedLabels.takeRow(labels.row(row));
			}

			trees[i] = new DecisionTree();
			trees[i].train(baggedFeatures, baggedLabels);
		}
	}

	@Override
	void predict(double[] in, double[] out)
	{

	}
}
