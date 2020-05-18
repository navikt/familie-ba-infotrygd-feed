package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDate

interface Innhold {}

data class InnholdVedtak(val datoStartNyBA : LocalDate, val fnrStoenadsmottaker: String) : Innhold {
    constructor(datoStartNyBA : LocalDate, fnrStoenadsmottaker: String, fnrBarn: String? = null, fnrmor: String? = null, fnrFar: String? = null)
            : this(datoStartNyBA, fnrStoenadsmottaker)
}

data class InnholdFødsel(val fnrBarn: String) : Innhold {
         constructor(fnrBarn: String, fnrmor: String? = null, fnrFar: String? = null): this(fnrBarn)
}
