package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common

import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.PaymentMethod

/** Textos de cada estado del pedido (los datos van en el modelo; los recursos, aquí). */
val OrderStatus.labelRes: Int
    get() = when (this) {
        OrderStatus.SOLICITADO -> R.string.status_received
        OrderStatus.TOMADO -> R.string.status_asigned
        OrderStatus.PREPARADO -> R.string.status_preparing
        OrderStatus.EN_CAMINO -> R.string.status_on_the_way
        OrderStatus.ENTREGADO -> R.string.status_delivered
    }

val OrderStatus.emoji: String
    get() = when (this) {
        OrderStatus.SOLICITADO -> "✅"
        OrderStatus.TOMADO -> "\uD83D\uDCDD"
        OrderStatus.PREPARADO -> "👨‍🍳"
        OrderStatus.EN_CAMINO -> "🛵"
        OrderStatus.ENTREGADO -> "🎉"
    }

val PaymentMethod.labelRes: Int
    get() = when (this) {
        PaymentMethod.CARD -> R.string.pay_card_title
        PaymentMethod.NEQUI -> R.string.pay_nequi_title
        PaymentMethod.DAVIPLATA -> R.string.pay_daviplata_title
        PaymentMethod.CASH -> R.string.pay_cash_title
    }
