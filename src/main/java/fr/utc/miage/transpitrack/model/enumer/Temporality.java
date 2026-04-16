package fr.utc.miage.transpitrack.model.enumer;

/**
 * Defines the recurrence period of a {@link fr.utc.miage.transpitrack.model.Goal}.
 * This allows users to set objectives that reset on a daily, weekly, monthly,
 * or yearly basis.
 */
public enum Temporality {

    /** The goal resets every day. */
    QUOTIDIEN,

    /** The goal resets every week. */
    HEBDOMADAIRE,

    /** The goal resets every month. */
    MENSUEL,

    /** The goal resets every year. */
    ANNUEL
}
