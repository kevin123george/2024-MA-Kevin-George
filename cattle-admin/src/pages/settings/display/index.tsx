// import CardWithForm from '@/components/card-with-form'
import { CardWithForm } from '@/components/card-with-form'
import ContentSection from '../components/content-section'
import { DisplayForm } from './display-form'

export default function SettingsDisplay() {
  return (
    <ContentSection
      title='Display'
      desc="Note :Applying the changes will restart the application!!"
    >
      {/* <DisplayForm /> */}
      <CardWithForm />
    </ContentSection>
  )
}
