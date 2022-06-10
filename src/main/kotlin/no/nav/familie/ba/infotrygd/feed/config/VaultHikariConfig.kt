package no.nav.familie.ba.infotrygd.feed.config

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties
import org.springframework.context.annotation.Configuration
import org.springframework.vault.core.lease.SecretLeaseContainer
import org.springframework.vault.core.lease.domain.RequestedSecret.rotating
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent

@Configuration
@ConditionalOnProperty(name = ["spring.cloud.vault.enabled"])
class VaultHikariConfig(
    private val container: SecretLeaseContainer,
    private val hikariDataSource: HikariDataSource,
    private val props: VaultDatabaseProperties
) : InitializingBean {

    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun afterPropertiesSet() {
        val secret = rotating(props.backend + "/creds/" + props.role)
        container.addLeaseListener { leaseEvent ->
            if (leaseEvent.source === secret && leaseEvent is SecretLeaseCreatedEvent) {
                log.info("Rotating credentials for path: " + leaseEvent.getSource().path)
                val username = leaseEvent.secrets["username"].toString()
                val password = leaseEvent.secrets["password"].toString()
                hikariDataSource.username = username
                hikariDataSource.password = password
                hikariDataSource.hikariConfigMXBean.setUsername(username)
                hikariDataSource.hikariConfigMXBean.setPassword(password)
                hikariDataSource.hikariPoolMXBean.softEvictConnections()
            }
        }
        container.addRequestedSecret(secret)
    }

    override fun toString(): String {
        return javaClass.simpleName + " [container=" +
            container + ", hikariDataSource=" +
            hikariDataSource + ", props=" +
            props + "]"
    }
}
