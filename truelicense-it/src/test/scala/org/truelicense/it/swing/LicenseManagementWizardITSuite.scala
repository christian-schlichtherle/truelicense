/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.swing

import java.awt.{Component, EventQueue}
import java.util.Date
import javax.swing._

import org.netbeans.jemmy._
import org.netbeans.jemmy.operators._
import org.netbeans.jemmy.util._
import org.scalatest.Matchers._
import org.scalatest._
import org.truelicense.api._
import org.truelicense.it.core.TestContext
import org.truelicense.it.swing.LicenseManagementWizardITSuite._
import org.truelicense.spi.io.MemoryStore
import org.truelicense.swing._
import org.truelicense.ui.LicenseWizardMessage
import org.truelicense.ui.LicenseWizardMessage._
import org.truelicense.ui.wizard.WizardMessage._

/** @author Christian Schlichtherle */
abstract class LicenseManagementWizardITSuite
  extends WordSpec
  with BeforeAndAfter
{ this: TestContext[_] =>

  val laf = UIManager.getLookAndFeel
  var outputLicense: License = _
  var manager: ConsumerLicenseManager = _
  var wizard: LicenseManagementWizard = _
  var dialog: JDialogOperator = _
  var cancelButton, backButton, nextButton: AbstractButtonOperator = _

  JemmyProperties.setCurrentOutput(TestOut.getNullOutput) // shut up!

  before {
    val store = new MemoryStore
    outputLicense = (vendorManager generator inputLicense writeTo store).license
    manager = consumerManager()
    manager install store
    EventQueue invokeLater new Runnable {
      override def run() {
        UIManager setLookAndFeel UIManager.getSystemLookAndFeelClassName
        wizard = newLicenseManagementWizard(manager)
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

  private def inputLicense = {
    // Don't subclass - wouldn't work with XML serialization
    val l = managementContext.license
    l.setInfo("Hello world!")
    l
  }

  after {
    cancelButton doClick ()
    dialog.isVisible should equal (false)
    wizard.getReturnCode should be (LicenseManagementWizard.CANCEL_RETURN_CODE)
    UIManager setLookAndFeel laf
  }

  "A license wizard" when {
    "using a consumer license manager with an installed license key" when {
      "showing" should {
        "be modal" in {
          dialog.isModal should equal (true)
        }

        "have a title which includes the license management subject" in {
          dialog.getTitle should include (managementContext.subject)
        }

        "have its back button disabled" in {
          backButton.isEnabled should equal (false)
        }

        "have its next button enabled" in {
          nextButton.isEnabled should equal (true)
        }

        "have its cancel button enabled" in {
          cancelButton.isEnabled should equal (true)
        }

        "show its welcome panel and hide the other panels" in {
          welcomePanel.isVisible should equal (true)
          installPanel.isVisible should equal (false)
          displayPanel.isVisible should equal (false)
          uninstallPanel.isVisible should equal (false)
        }

        "have a visible, non-empty prompt on its welcome panel" in {
          dialog.getQueueTool waitEmpty ()
          val prompt = waitTextComponent(welcomePanel, welcome_prompt)
          prompt.isVisible should equal (true)
          prompt.getText should not be 'empty
        }

        "have both the install and display buttons enabled" in {
          val installSelector = waitButton(welcomePanel, welcome_install)
          val displaySelector = waitButton(welcomePanel, welcome_display)
          installSelector.isEnabled should equal (true)
          displaySelector.isEnabled should equal (true)
        }

        "switch to the install panel when requested" in {
          val installSelector = waitButton(welcomePanel, welcome_install)
          installSelector.isVisible should equal (true)
          installSelector.isEnabled should equal (true)
          installSelector.isSelected should equal (false)
          installSelector doClick ()
          nextButton doClick ()
          welcomePanel.isVisible should equal (false)
          installPanel.isVisible should equal (true)
          waitButton(installPanel, install_install).isEnabled should equal (false)
        }

        "switch to the display panel by default and display the license content" in {
          val displaySelector = waitButton(welcomePanel, welcome_display)
          displaySelector.isVisible should equal (true)
          displaySelector.isEnabled should equal (true)
          displaySelector.isSelected should equal (true)
          nextButton doClick ()
          welcomePanel.isVisible should equal (false)
          displayPanel.isVisible should equal (true)

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

        "switch to the uninstall panel when requested" in {
          val uninstallSelector = waitButton(welcomePanel, welcome_uninstall)
          uninstallSelector.isVisible should equal (true)
          uninstallSelector.isEnabled should equal (true)
          uninstallSelector.isSelected should equal (false)
          uninstallSelector doClick ()
          nextButton doClick ()
          welcomePanel.isVisible should equal (false)
          uninstallPanel.isVisible should equal (true)
          waitButton(uninstallPanel, uninstall_uninstall).isEnabled should equal (true)
        }
      }
    }
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
}

/** @author Christian Schlichtherle */
object LicenseManagementWizardITSuite {

  private def newLicenseManagementWizard(manager: ConsumerLicenseManager) = {
    val wizard = new LicenseManagementWizard(manager)
    wizard.isUninstallButtonVisible should equal (false)
    wizard.setUninstallButtonVisible(true)
    wizard.isUninstallButtonVisible should equal (true)
    wizard
  }

  private def waitButton(cont: ContainerOperator, key: Enum[_]) =
    new AbstractButtonOperator(cont, new NameComponentChooser(key.name))

  private def waitTextComponent(cont: ContainerOperator, key: Enum[_]) =
    new JTextComponentOperator(cont, new NameComponentChooser(key.name))
}
