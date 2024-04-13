/*
 * Copyright 2023 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.ai.vectorstore.filter.converter;

import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.Filter.Group;
import org.springframework.ai.vectorstore.filter.Filter.Key;

/**
 * Converts {@link Expression} into PgVector metadata filter expression format.
 * (https://www.postgresql.org/docs/current/functions-json.html)
 *
 * @author Christian Tzolov
 */
public class PgVectorFilterExpressionConverter extends AbstractFilterExpressionConverter {

	@Override
	protected void doExpression(Expression expression, StringBuilder context) {
		this.convertOperand(expression.left(), context);
		context.append(getOperationSymbol(expression));
		this.convertOperand(expression.right(), context);
	}

	private String getOperationSymbol(Expression exp) {
		switch (exp.type()) {
			case AND:
				return " AND ";
			case OR:
				return " OR ";
			case EQ:
				return " = ";
			case NE:
				return " != ";
			case LT:
				return " < ";
			case LTE:
				return " <= ";
			case GT:
				return " > ";
			case GTE:
				return " >= ";
			case IN:
				return " IN ";
			case NIN:
				return " NOT IN ";
			default:
				throw new RuntimeException("Not supported expression type: " + exp.type());
		}
	}

	@Override
	protected void doKey(Key key, StringBuilder context) {
		context.append("metadata::jsonb->>'");
		if (hasOuterQuotes(key.key())) {
			context.append(removeOuterQuotes(key.key()));
		}
		else {
			context.append(key.key());
		}
		context.append('\'');
	}

	@Override
	protected void doSingleValue(Object value, StringBuilder context) {
		if (value instanceof String) {
			context.append(String.format("\'%s\'", value));
		}
		else {
			context.append(value);
		}
	}

	@Override
	protected void doStartGroup(Group group, StringBuilder context) {
		context.append("(");
	}

	@Override
	protected void doEndGroup(Group group, StringBuilder context) {
		context.append(")");
	}

	@Override
	protected void doStartValueRange(Filter.Value listValue, StringBuilder context) {
		context.append("(");
	}

	@Override
	protected void doEndValueRange(Filter.Value listValue, StringBuilder context) {
		context.append(")");
	}

}