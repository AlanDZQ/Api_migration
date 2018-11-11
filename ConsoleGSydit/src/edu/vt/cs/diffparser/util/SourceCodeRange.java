package edu.vt.cs.diffparser.util;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SwitchStatement;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;

public class SourceCodeRange implements Serializable, Comparable<SourceCodeRange>{

	public static SourceCodeRange DefaultRange = new SourceCodeRange(0, 0);

	private static final long serialVersionUID = 1L;
	public int startPosition;
	public int length;

	public static SourceCodeRange getDefaultScr() {
		return new SourceCodeRange(0, 0);
	}

	public SourceCodeRange(SourceCodeRange scr) {
		this.startPosition = scr.startPosition;
		this.length = scr.length;
	}

	public SourceCodeRange(int startPosition, int length) {
		this.startPosition = startPosition;
		this.length = length;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SourceCodeRange))
			return false;
		SourceCodeRange other = (SourceCodeRange) obj;
		if (this.startPosition != other.startPosition)
			return false;
		if (this.length != other.length)
			return false;
		return true;
	}

	public int hashCode() {
		return this.startPosition * 1000 + this.length;
	}

	public boolean isInside(SourceCodeRange other) {
		return startPosition >= other.startPosition
				&& startPosition + length <= other.startPosition + other.length;
	}

	public String toString() {
		return "start position = " + this.startPosition + "  length = "
				+ this.length;
	}

	/**
	 * This method does not handle the containment relations between two objects
	 */
	@Override
	public int compareTo(SourceCodeRange obj) {
		SourceCodeRange other = (SourceCodeRange) obj;
		if (this.startPosition < other.startPosition)
//				&& this.startPosition + this.length < other.startPosition
//						+ other.length
			return -1;
		if (this.startPosition > other.startPosition
				&& this.startPosition + this.length > other.startPosition
						+ other.length)
			return 1;
		return 0;
	}
	
	public SourceRange converToSourceRange() {
		return new SourceRange(startPosition, startPosition + length - 1);
	}
	
	public static SourceCodeRange convert(SourceCodeEntity entity) {
		return new SourceCodeRange(entity.getStartPosition(), entity.getEndPosition() - entity.getStartPosition() + 1);
	}
	
	public static SourceCodeRange convert(ASTNode node) {
//		System.out.println(node);
		return new SourceCodeRange(node.getStartPosition(), node.getLength());
	}
	
	public static SourceCodeRange convert(SourceRange r) {
		return new SourceCodeRange(r.getStart(), r.getEnd() - r.getStart() + 1);
	}
	
	/**
	 * Notice that this is simply converting the entity's source range to source code range.
	 * Instead, for compound statements like if-stmt and for-stmt, the return value is the range
	 * of the condition(s)
	 * @param entity
	 * @return
	 */
//	public static SourceCodeRange getRange(SourceCodeEntity entity, CompilationUnit cu) {		
//		SourceCodeRange scr = convert(entity);
//		ASTNodeFinder finder = new ASTNodeFinder();
//		ASTNode n = finder.lookforASTNode(cu, scr);
//		Expression e = null;
//		switch(n.getNodeType()) {
//		case ASTNode.DO_STATEMENT:
//			e = ((DoStatement)n).getExpression();
//			scr = new SourceCodeRange(e.getStartPosition(), e.getLength());
//			break;
//		case ASTNode.ENHANCED_FOR_STATEMENT:
//			e = ((EnhancedForStatement)n).getExpression();
//			scr = new SourceCodeRange(e.getStartPosition(), e.getLength());
//		case ASTNode.SWITCH_STATEMENT:
//			e = ((SwitchStatement)n).getExpression();
//			scr = new SourceCodeRange(e.getStartPosition(), e.getLength());
//			break;
//		default: 
//			break;	
//		}
//		return scr;
//	}
}
