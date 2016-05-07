package io.subutai.common.datatypes;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.subutai.common.security.relation.Trait;


@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface RelationDeclaration
{
    /**
     * Relationship query, that queries permissions exists between entity
     */
    String rql() default "";

    Trait[] conditions();


}
