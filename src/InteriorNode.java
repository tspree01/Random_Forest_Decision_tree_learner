class InteriorNode extends Node
{
	int attribute; // which attribute to divide on
	double pivot; // which value to divide on
	Node a;
	Node b;

	InteriorNode(Node a, Node b, int attribute, double pivot)
	{
		this.attribute = attribute;
		this.pivot = pivot;
		this.a = a;
		this.b = b;
	}

	@Override
	boolean isInterior()
	{
		return true;
	}

	boolean isLeaf() { return false; }
}