import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}

import javax.inject.Inject

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    bind(classOf[ActorSystem]).toProvider(classOf[ActorSystemProvider]).asEagerSingleton()
    bind(classOf[Materializer]).toProvider(classOf[MaterializerProvider]).asEagerSingleton()
  }
}

class ActorSystemProvider @Inject()(environment: Environment, configuration: Configuration) extends javax.inject.Provider[ActorSystem] {
  override def get(): ActorSystem = ActorSystem("library-system")
}

class MaterializerProvider @Inject()(implicit system: ActorSystem) extends javax.inject.Provider[Materializer] {
  override def get(): Materializer = akka.stream.Materializer.matFromSystem(system)
}