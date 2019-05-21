package problems.qbfpt.solvers;

import java.io.IOException;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;
import problems.qbfpt.QBFPT;

public class Gurobi_QBFPT {

	public static GRBEnv env;
	public static GRBModel model;
	public GRBVar[] x;
	public QBFPT problem;

	public Gurobi_QBFPT(String filename) throws IOException {
		this.problem = new QBFPT(filename);
	}

	public static void main(String[] args) throws IOException, GRBException {
		
		// instance name
		Gurobi_QBFPT gurobi = new Gurobi_QBFPT("instances/qbf400");
		
		env = new GRBEnv("mip1.log");
		model = new GRBModel(env);
		
		// execution time in seconds 
		model.getEnv().set(GRB.DoubleParam.TimeLimit, 30 * 60.0);

		
		// generate the model
		gurobi.populateNewModel(model);

		// write model to file
		model.write("model.lp");

		model.optimize();

		System.out.println("\n\nZ* = " + model.get(GRB.DoubleAttr.ObjVal));
		
		System.out.print("X = [");
		for (int i = 0; i < gurobi.problem.size; i++) {
	          System.out.print((gurobi.x[i].get(GRB.DoubleAttr.X)>0.5 ? 1.0 : 0) + ", ");
		}
		System.out.print("]");

		model.dispose();
		env.dispose();

	}

	private void populateNewModel(GRBModel model) throws GRBException {

		// variables
		x = new GRBVar[problem.size];

		for (int i = 0; i < problem.size; i++) {
			x[i] = model.addVar(0, 1, 0.0f, GRB.BINARY, "x[" + i + "]");
		}
		model.update();

		// objective functions
		GRBQuadExpr obj = new GRBQuadExpr();
		for (int i = 0; i < problem.size; i++) {
			for (int j = i; j < problem.size; j++) {
				obj.addTerm(problem.A[i][j], x[i], x[j]);
			}
		}
		
		for (int i = 0; i < problem.prohibited_triples.length; i++)
		{
			GRBLinExpr lhs = new GRBLinExpr();
			lhs.addTerm(1.0, x[problem.prohibited_triples[i][0]]);
			lhs.addTerm(1.0, x[problem.prohibited_triples[i][1]]);
			lhs.addTerm(1.0, x[problem.prohibited_triples[i][2]]);
			model.addConstr(lhs, GRB.LESS_EQUAL, 2.0, "prohibited " + i);
		}

		model.setObjective(obj);
		model.update();

		// maximization objective function
		model.set(GRB.IntAttr.ModelSense, -1);

	}

}
