package problems.qbf.solvers;

import java.io.IOException;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;
import problems.qbf.QBF;

public class Gurobi_QBF {

	public static GRBEnv env;
	public static GRBModel model;
	public GRBVar[] x;
	public QBF problem;

	public Gurobi_QBF(String filename) throws IOException {
		this.problem = new QBF(filename);
	}

	public static void main(String[] args) throws IOException, GRBException {
		
		// instance name
		Gurobi_QBF gurobi = new Gurobi_QBF("instances/qbf400");
		
		env = new GRBEnv("mip1.log");
		model = new GRBModel(env);
		
		// execution time in seconds 
		model.getEnv().set(GRB.DoubleParam.TimeLimit, 600.0);

		
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

		model.setObjective(obj);
		model.update();

		// maximization objective function
		model.set(GRB.IntAttr.ModelSense, -1);

	}

}
