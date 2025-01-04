/**
 * Z PL/SQL Analyzer
 * Copyright (C) 2015-2025 Felipe Zorzo
 * mailto:felipe AT felipezorzo DOT com DOT br
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plsqlopen.checks

import com.felipebz.flr.api.AstNode
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar
import org.sonar.plugins.plsqlopen.api.annotations.*
import org.sonar.plugins.plsqlopen.api.symbols.Scope
import org.sonar.plugins.plsqlopen.api.symbols.Symbol

@Rule(priority = Priority.MAJOR, tags = [Tags.UNUSED])
@ConstantRemediation("2min")
@RuleInfo(scope = RuleInfo.Scope.ALL)
@ActivatedByDefault
class UnusedVariableCheck : AbstractBaseCheck() {

    override fun leaveFile(node: AstNode) {
        val scopes = context.symbolTable.scopes
        for (scope in scopes) {
            if (scope.type !in arrayOf(PlSqlGrammar.CREATE_PACKAGE, PlSqlGrammar.FOR_STATEMENT)) {
                checkScope(scope)
            }
        }
    }

    private fun checkScope(scope: Scope) {
        val symbols = scope.getSymbols(Symbol.Kind.VARIABLE)
        for (symbol in symbols) {
            if (symbol.usages.isEmpty()) {
                addIssue(symbol.declaration, getLocalizedMessage(), symbol.name)
            }
        }
    }

}
