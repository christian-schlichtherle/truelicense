/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.swing

import java.awt.{Component, EventQueue, GraphicsEnvironment}
import java.util.Date
import javax.swing._

import net.truelicense.api.{ConsumerLicenseManager, License, LicenseManagementException}
import net.truelicense.it.core.TestContext
import net.truelicense.it.swing.LicenseManagementWizardITSuite._
import net.truelicense.spi.io.MemoryStore
import net.truelicense.swing.LicenseManagementWizard
import net.truelicense.ui.LicenseWizardMessage
import net.truelicense.ui.LicenseWizardMessage._
import net.truelicense.ui.wizard.WizardMessage._
import org.netbeans.jemmy._
import org.netbeans.jemmy.operators._
import org.netbeans.jemmy.util._
import org.scalactic.source.Position
import org.scalatest.Matchers._
import org.scalatest._

/** @author Christian Schlichtherle */
abstract class LicenseManagementWizardITSuite
  extends WordSpec
  with BeforeAndAfter
{ this: TestContext[_] =>

  private val laf = UIManager.getLookAndFeel
  private var outputLicense: License = _
  private var manager: ConsumerLicenseManager = _
  private var wizard: LicenseManagementWizard = _
  private var dialog: JDialogOperator = _
  private var cancelButton, backButton, nextButton: AbstractButtonOperator = _

  JemmyProperties.setCurrentOutput(TestOut.getNullOutput) // shut up!

  before {
    val store = new MemoryStore
    outputLicense = (vendorManager generateKeyFrom inputLicense saveTo store).license
    manager = consumerManager(store)
    EventQueue invokeLater new Runnable {
      override def run() {
        UIManager setLookAndFeel UIManager.getSystemLookAndFeelClassName
        wizard = newLicenseManagementWizard(manager)
        wizard showModalDialog ()
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
    wizard.getReturnCode should be (LicenseManagementWizard.CANCEL_RETURN_CODE)
    UIManager setLookAndFeel laf
  }

  "A license wizard" when {
    "using a consumer license manager with an installed license key" when {
      "showing" should {
        "be modal" ifNotHeadless {
          dialog.isModal shouldBe true
        }

        "have a title which includes the licensing management subject" ifNotHeadless {
          dialog.getTitle should include (managementContext.subject)
        }

        "have its back button disabled" ifNotHeadless {
          backButton.isEnabled shouldBe false
        }

        "have its next button enabled" ifNotHeadless {
          nextButton.isEnabled shouldBe true
        }

        "have its cancel button enabled" ifNotHeadless {
          cancelButton.isEnabled shouldBe true
        }

        "show its welcome panel and hide the other panels" ifNotHeadless {
          welcomePanel.isVisible shouldBe true
          installPanel.isVisible shouldBe false
          displayPanel.isVisible shouldBe false
          uninstallPanel.isVisible shouldBe false
        }

        "have a visible, non-empty prompt on its welcome panel" ifNotHeadless {
          dialog.getQueueTool waitEmpty ()
          val prompt = waitTextComponent(welcomePanel, welcome_prompt)
          prompt.isVisible shouldBe true
          prompt.getText should not be 'empty
        }

        "have both the install and display buttons enabled" ifNotHeadless {
          val installSelector = waitButton(welcomePanel, welcome_install)
          val displaySelector = waitButton(welcomePanel, welcome_display)
          installSelector.isEnabled shouldBe true
          displaySelector.isEnabled shouldBe true
        }

        "switch to the install panel when requested" ifNotHeadless {
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

        "switch to the display panel by default and display the license content" ifNotHeadless {
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
            display_dateTimeFormat(managementContext.subject, date)

          waitText(display_holder) should be (toString(outputLicense.getHolder))
          waitText(display_subject) should be (toString(outputLicense.getSubject))
          waitText(display_consumer) should be (toString(outputLicense.getConsumerType) + " / " + outputLicense.getConsumerAmount)
          waitText(display_notBefore) should be (format(outputLicense.getNotBefore))
          waitText(display_notAfter) should be (format(outputLicense.getNotAfter))
          waitText(display_issuer) should be (toString(outputLicense.getIssuer))
          waitText(display_issued) should be (format(outputLicense.getIssued))
          waitText(display_info) should be (toString(outputLicense.getInfo))
        }

        "switch to the uninstall panel when requested" ifNotHeadless {
          val uninstallSelector = waitButton(welcomePanel, welcome_uninstall)
          uninstallSelector.isVisible shouldBe true
          uninstallSelector.isEnabled shouldBe true
          uninstallSelector.isSelected shouldBe false
          uninstallSelector doClick ()
          nextButton doClick ()
          welcomePanel.isVisible shouldBe false
          uninstallPanel.isVisible shouldBe true
          waitButton(uninstallPanel, uninstall_uninstall) doClick ()
          Thread sleep 100
          intercept[LicenseManagementException] { manager load () }
        }
      }
    }
  }

  private def inputLicense = {
    // Don't subclass - wouldn't work with XML serialization
    val l = managementContext.license
    l.setInfo("Hello world!")
    l
  }

  private implicit class WithText(text: String) {

    def ifNotHeadless(block: => Any)(implicit pos: Position): Unit = {
      if (GraphicsEnvironment.isHeadless) {
        text ignore block
      } else {
        text in block
      }
    }
  }

  private def welcomePanel = waitPanel("WelcomePanel")
  private def installPanel = waitPanel("InstallPanel")
  private def displayPanel = waitPanel("DisplayPanel")
  private def uninstallPanel = waitPanel("UninstallPanel")

  private def waitPanel(name: String) =
    new JComponentOperator(dialog, new ComponentChooser {
      val delegate = new NameComponentChooser(name)

      def checkComponent(comp: Component): Boolean =
        comp match {
          case panel: JPanel => delegate checkComponent panel
          case _ => false
        }

      def getDescription = "Chooses a JPanel by its name."
    })

  private def toString(obj: AnyRef) = if (null ne obj) obj.toString else ""
}

/** @author Christian Schlichtherle */
object LicenseManagementWizardITSuite {

  private def newLicenseManagementWizard(manager: ConsumerLicenseManager) = {
    val wizard = new LicenseManagementWizard(manager)
    wizard.isUninstallButtonVisible shouldBe false
    wizard setUninstallButtonVisible true
    wizard.isUninstallButtonVisible shouldBe true
    wizard
  }

  private def waitButton(cont: ContainerOperator, key: Enum[_]) =
    new AbstractButtonOperator(cont, new NameComponentChooser(key.name))

  private def waitTextComponent(cont: ContainerOperator, key: Enum[_]) =
    new JTextComponentOperator(cont, new NameComponentChooser(key.name))
}
