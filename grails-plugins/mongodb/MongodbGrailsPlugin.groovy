
import org.grails.datastore.gorm.mongo.plugin.support.MongoMethodsConfigurer
import org.grails.datastore.gorm.mongo.plugin.support.MongoOnChangeHandler
import org.grails.datastore.gorm.mongo.plugin.support.MongoSpringConfigurer
import grails.converters.*
import com.mongodb.DBObject
class MongodbGrailsPlugin {
    def license = "Apache 2.0 License"
    def organization = [name: "SpringSource", url: "http://www.springsource.org/"]
    def developers = [
        [name: "Graeme Rocher", email: "grocher@vmware.com"]]
    def issueManagement = [system: "JIRA", url: "http://jira.grails.org/browse/GPMONGODB"]
    def scm = [url: "https://github.com/grails/grails-data-mapping"]

    def version = "2.0.0"
    def grailsVersion = "2.3.2 > *"
    def observe = ['services', 'domainClass']
    def loadAfter = ['domainClass', 'hibernate', 'hibernate4', 'services', 'cloudFoundry']
    def author = "Graeme Rocher"
    def authorEmail = "graeme.rocher@springsource.com"
    def title = "MongoDB GORM"
    def description = 'A plugin that integrates the Mongo document datastore into Grails, providing a GORM API onto it'

    def documentation = "http://projects.spring.io/grails-data-mapping/mongo/manual/index.html"

    def doWithSpring = new MongoSpringConfigurer().getConfiguration()

    def doWithApplicationContext = { 
        JSON.registerObjectMarshaller DBObject, { it.toMap() }
    }

    def doWithDynamicMethods = { ctx ->
        def datastore = ctx.mongoDatastore
        def transactionManager = ctx.mongoTransactionManager
        def methodsConfigurer = new MongoMethodsConfigurer(datastore, transactionManager)    
        methodsConfigurer.hasExistingDatastore = manager.hasGrailsPlugin("hibernate") || manager.hasGrailsPlugin("hibernate4")
        def foe = application?.config?.grails?.gorm?.failOnError
        methodsConfigurer.failOnError = foe instanceof Boolean ? foe : false
        methodsConfigurer.configure()
    }

    def onChange = { event ->
        if(event.ctx) {
            new MongoOnChangeHandler(event.ctx.mongoDatastore, event.ctx.mongoTransactionManager).onChange(delegate, event)            
        }
    }   
}
