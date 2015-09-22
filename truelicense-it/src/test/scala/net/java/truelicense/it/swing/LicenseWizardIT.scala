/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.swing

import java.awt.{Component, EventQueue}
import java.util.Date
import javax.swing._

import net.java.truelicense.core._
import net.java.truelicense.core.io.MemoryStore
import net.java.truelicense.it.core.TestContext.test1234
import net.java.truelicense.it.swing.LicenseWizardIT._
import net.java.truelicense.swing._
import net.java.truelicense.ui.LicenseWizardMessage
import net.java.truelicense.ui.LicenseWizardMessage._
import net.java.truelicense.ui.wizard.WizardMessage._
import org.junit.runner._
import org.netbeans.jemmy._
import org.netbeans.jemmy.operators._
import org.netbeans.jemmy.util._
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class LicenseWizardIT extends WordSpec with BeforeAndAfter {

  val laf = UIManager.getLookAndFeel
  var license: License = _
  var manager: LicenseConsumerManager = _
  var wizard: LicenseWizard = _
  var dialog: JDialogOperator = _
  var cancelButton, backButton, nextButton: AbstractButtonOperator = _

  JemmyProperties.setCurrentOutput(TestOut.getNullOutput) // shut up!

  before {
    val store = new MemoryStore
    license = vendorManager create (newLicense, store)
    manager = consumerManager
    manager install store
    EventQueue invokeLater new Runnable {
      override def run() {
        UIManager setLookAndFeel UIManager.getSystemLookAndFeelClassName
        wizard = newLicenseWizard(manager)
        wizard.showModalDialog
      }
    }
    dialog = new JDialogOperator()
    cancelButton = waitButton(dialog, wizard_cancel)
    backButton = waitButton(dialog, wizard_back)
    nextButton = waitButton(dialog, wizard_next)
    // Defer test execution to allow asynchronous license certificate
    // verification to complete.
    Thread sleep 100
  }

  after {
    cancelButton doClick ()
    dialog.isVisible shouldBe false
    wizard.getReturnCode should be (LicenseWizard.CANCEL_RETURN_CODE)
    UIManager setLookAndFeel laf
  }

  def welcomePanel = waitPanel("WelcomePanel")
  def installPanel = waitPanel("InstallPanel")
  def displayPanel = waitPanel("DisplayPanel")
  def uninstallPanel = waitPanel("UninstallPanel")

  def waitPanel(name: String) =
    new JComponentOperator(dialog, new ComponentChooser {
      val delegate = new NameComponentChooser(name)

      def checkComponent(comp: Component) =
        comp match {
          case panel: JPanel => delegate.checkComponent(panel)
          case _ => false
        }

      def getDescription = "Chooses a JPanel by its name."
    })

  def toString(obj: AnyRef) = if (null ne obj) obj.toString else ""

  "A license wizard" when {
    "using a license consumer manager with an installed license key" when {
      "showing" should {
        "be modal" in {
          dialog.isModal shouldBe true
        }

        "have a title which includes the licensing subject" in {
          dialog.getTitle should include (manager.subject)
        }

        "have its back button disabled" in {
          backButton.isEnabled shouldBe false
        }

        "have its next button enabled" in {
          nextButton.isEnabled shouldBe true
        }

        "have its cancel button enabled" in {
          cancelButton.isEnabled shouldBe true
        }

        "show its welcome panel and hide the other panels" in {
          welcomePanel.isVisible shouldBe true
          installPanel.isVisible shouldBe false
          displayPanel.isVisible shouldBe false
          uninstallPanel.isVisible shouldBe false
        }

        "have a visible, non-empty prompt on its welcome panel" in {
          dialog.getQueueTool waitEmpty ()
          val prompt = waitTextComponent(welcomePanel, welcome_prompt)
          prompt.isVisible shouldBe true
          prompt.getText should not be 'empty
        }

        "have both the install and display buttons enabled" in {
          val installSelector = waitButton(welcomePanel, welcome_install)
          val displaySelector = waitButton(welcomePanel, welcome_display)
          installSelector.isEnabled shouldBe true
          displaySelector.isEnabled shouldBe true
        }

        "switch to the install panel when requested" in {
          val installSelector = waitButton(welcomePanel, welcome_install)
          installSelector.isVisible shouldBe true
          installSelector.isEnabled shouldBe true
          installSelector.isSelected shouldBe false
          installSelector doClick ()
          nextButton doClick ()
          welcomePanel.isVisible shouldBe false
          installPanel.isVisible shouldBe true
          waitButton(installPanel, install_install).isEnabled shouldBe false
        }

        "switch to the display panel by default and display the license content" in {
          val displaySelector = waitButton(welcomePanel, welcome_display)
          displaySelector.isVisible shouldBe true
          displaySelector.isEnabled shouldBe true
          displaySelector.isSelected shouldBe true
          nextButton doClick ()
          welcomePanel.isVisible shouldBe false
          displayPanel.isVisible shouldBe true

          def waitText(key: LicenseWizardMessage) =
            waitTextComponent(displayPanel, key).getText

          def format(date: Date) =
            display_dateTimeFormat(manager.subject, date)

          waitText(display_holder) should be (toString(license.getHolder))
          waitText(display_subject) should be (toString(license.getSubject))
          waitText(display_consumer) should be (toString(license.getConsumerType) + " / " + license.getConsumerAmount)
          waitText(display_notBefore) should be (format(license.getNotBefore))
          waitText(display_notAfter) should be (format(license.getNotAfter))
          waitText(display_issuer) should be (toString(license.getIssuer))
          waitText(display_issued) should be (format(license.getIssued))
          waitText(display_info) should be (toString(license.getInfo))
        }

        "switch to the uninstall panel when requested" in {
          val uninstallSelector = waitButton(welcomePanel, welcome_uninstall)
          uninstallSelector.isVisible shouldBe true
          uninstallSelector.isEnabled shouldBe true
          uninstallSelector.isSelected shouldBe false
          uninstallSelector doClick ()
          nextButton doClick ()
          welcomePanel.isVisible shouldBe false
          uninstallPanel.isVisible shouldBe true
          waitButton(uninstallPanel, uninstall_uninstall).isEnabled shouldBe true
        }
      }
    }
  }
}

/** @author Christian Schlichtherle */
object LicenseWizardIT {

  private def managementContext = new V2XmlLicenseManagementContext("subject")

  private def vendorManager = {
    val vc = managementContext.vendor
    import vc._
    manager(keyStore(resource(prefix + "private.ks"), null, test1234, "mykey", test1234),
      pbe("PBEWithSHA1AndDESede", test1234)
    )
  }

  private def consumerManager = {
    val cc = managementContext.consumer
    import cc._
    manager(keyStore(resource(prefix + "public.ks"), null, test1234, "mykey"),
      pbe("PBEWithSHA1AndDESede", test1234),
      new MemoryStore)
  }

  private def newLicense = {
    // Don't subclass - wouldn't work with XML serialization
    val l = new License
    import l._
    setInfo("Hello world!")
    l
  }

  private def newLicenseWizard(manager: LicenseConsumerManager) = {
    val wizard = new LicenseWizard(manager)
    wizard.isUninstallButtonVisible shouldBe false
    wizard.setUninstallButtonVisible(true)
    wizard.isUninstallButtonVisible shouldBe true
    wizard
  }

  private def waitButton(cont: ContainerOperator, key: Enum[_]) =
    new AbstractButtonOperator(cont, new NameComponentChooser(key.name))

  private def waitTextComponent(cont: ContainerOperator, key: Enum[_]) =
    new JTextComponentOperator(cont, new NameComponentChooser(key.name))

  private def prefix = classOf[LicenseWizardIT].getPackage.getName.replace('.', '/') + '/'
}
